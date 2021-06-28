package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.Cache;
import cn.sliew.milky.cache.CacheLoader;
import cn.sliew.milky.common.exception.Rethrower;
import cn.sliew.milky.log.Logger;
import cn.sliew.milky.log.LoggerFactory;
import io.lettuce.core.Limit;
import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.metrics.MicrometerCommandLatencyRecorder;
import io.lettuce.core.metrics.MicrometerOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.support.BoundedPoolConfig;
import io.micrometer.core.instrument.MeterRegistry;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static cn.sliew.milky.common.check.Ensures.checkNotNull;

/**
 * redis缓存没有使用常用的strings类型来搞
 * 使用了hash作为缓存存储模式带来一个问题：redis官方至今没有打算支持hash的field的ttl。
 * 因此需要自己实现针对hash的field的ttl。
 * 为value对象创建包装类ValueWrapper，记录过期时间戳。获取缓存对象的时候查看对象是否已经过期。
 * 对于每个key对象放入一个sortset，以过期时间戳作为分数，创建定时任务定期从sortset和hash
 * 中移除缓存对象。
 * 为了保证数据不会出现永久停留在redis中的问题，设置缓存的过期时间统一为凌晨3点，缓存的凌晨过期
 * 不会造成业务的突然抖动，也能定时清理所有的缓存对象
 * todo 考虑到对象双写的问题，需要将同时操作sortset和hash的对象移除功能处理为一个
 * todo 原子操作，需要使用lua脚本来搞。
 * <p>
 * 时间轮是一秒跳一次，每个任务执行完毕后设置的是一秒后执行下一个任务，因此最快两个任务的执行间隔为2s。
 * 回头需要研究下spring中的redis cache是如何操作的。
 */
public class LettuceCache<K, V> implements Cache<K, V> {

    private static final Logger log = LoggerFactory.getLogger(LettuceCache.class);

    /**
     * 时间轮，用来定时处理sortset和hash中的过期对象。
     */
    private final HashedWheelTimer timer;

    private DefaultLettuceConnectionFactory connectionFactory;

    private final LettuceCacheOptions<K, V> options;

    public LettuceCache(LettuceCacheOptions<K, V> options) {
        this.options = checkNotNull(options, () -> "options can't be null");
        connect(ClientResources.create());

        this.timer = new HashedWheelTimer(1, TimeUnit.SECONDS, 64);
        this.timer.newTimeout(new ExpireTimeTask(), 1L, TimeUnit.SECONDS);
        this.timer.start();
    }

    @Override
    public String name() {
        return this.options.getName();
    }

    private String hashKey() {
        return this.name() + "_hash";
    }

    private String sortsetKey() {
        return this.name() + "_sortset";
    }

    @Override
    public V get(K key) {
        try (LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection()) {
            LettuceCommandsWrapper commandsWrapper = connection.sync();
            ValueWrapper<V> valueWrapper = (ValueWrapper<V>) commandsWrapper.hget(hashKey(), key);
            if (valueWrapper == null) {
                return null;
            }
            if (valueWrapper.getExpireAt() <= System.nanoTime()) {
                remove(key);
                return null;
            }
            return valueWrapper.getValue();
        } catch (IOException e) {
            Rethrower.throwAs(e);
            // should never reach here
            return null;
        }
    }

    @Override
    public boolean containsKey(K key) {
        try (LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection()) {
            final LettuceCommandsWrapper commandsWrapper = connection.sync();
            if (commandsWrapper.hexists(hashKey(), key)) {
                ValueWrapper<V> valueWrapper = (ValueWrapper<V>) commandsWrapper.hget(hashKey(), key);
                if (valueWrapper.getExpireAt() <= System.nanoTime()) {
                    remove(key);
                    return false;
                }
                return valueWrapper.getValue() != null;
            }
            return false;
        } catch (IOException e) {
            Rethrower.throwAs(e);
            // should never reach here
            return false;
        }
    }

    @Override
    public V computeIfAbsent(K key, CacheLoader<K, V> loader, Duration expire) {
        V value = get(key);
        if (value == null) {
            synchronized (connectionFactory) {
                value = get(key);
                if (value == null) {
                    try {
                        value = loader.load(key);
                        put(key, value, expire);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
        return value;
    }

    @Override
    public void put(K key, V value) {
        try (LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection()) {
            final LettuceCommandsWrapper commandsWrapper = connection.sync();
            commandsWrapper.zadd(sortsetKey(), Long.MAX_VALUE, value);
            commandsWrapper.hset(hashKey(), key, new ValueWrapper<>(value, Long.MAX_VALUE));
        } catch (IOException e) {
            Rethrower.throwAs(e);
        }
    }

    @Override
    public void put(K key, V value, Duration expire) {
        try (LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection()) {
            final LettuceCommandsWrapper commandsWrapper = connection.sync();
            long expireAt = expire.toNanos() + System.nanoTime();
            commandsWrapper.zadd(sortsetKey(), expireAt, value);
            commandsWrapper.hset(hashKey(), key, new ValueWrapper<>(value, expireAt));
        } catch (IOException e) {
            Rethrower.throwAs(e);
        }
    }

    @Override
    public void remove(K key) {
        try (LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection()) {
            final LettuceCommandsWrapper commandsWrapper = connection.sync();
            commandsWrapper.hdel(hashKey(), key);
            commandsWrapper.zrem(sortsetKey(), key);
        } catch (IOException e) {
            Rethrower.throwAs(e);
        }
    }

    @Override
    public void removeAll(Iterable<K> keys) {
        try (LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection()) {
            final LettuceCommandsWrapper commandsWrapper = connection.sync();
            Object[] array = StreamSupport.stream(keys.spliterator(), false).toArray();
            commandsWrapper.hdel(hashKey(), array);
            commandsWrapper.zrem(sortsetKey(), array);
        } catch (IOException e) {
            Rethrower.throwAs(e);
        }
    }

    @Override
    public long size() {
        try (LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection()) {
            final LettuceCommandsWrapper commandsWrapper = connection.sync();
            return commandsWrapper.zcard(sortsetKey());
        } catch (IOException e) {
            Rethrower.throwAs(e);
            // should never reach here
            return 0L;
        }
    }

    @Override
    public boolean supportNullKey() {
        return false;
    }

    @Override
    public boolean supportNullValue() {
        return true;
    }

    /**
     * fixme hkeys hscan
     * todo 考虑使用sortset的key来搞。zscan
     */
    @Override
    public Iterator<K> keyIterator() {
        try (LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection()) {
            final LettuceCommandsWrapper commandsWrapper = connection.sync();
            return (Iterator<K>) commandsWrapper.hkeys(hashKey()).iterator();
        } catch (IOException e) {
            Rethrower.throwAs(e);
            // should never reach here
            return null;
        }
    }

    @Override
    public Iterator<K> hotKeyIterator(int n) {
        return Collections.emptyIterator();
    }

    /**
     * fixme 数据监控
     */
    @Override
    public void stats(MeterRegistry registry) {
        MicrometerOptions options = MicrometerOptions.create();
        ClientResources resources = ClientResources.builder()
                .commandLatencyRecorder(new MicrometerCommandLatencyRecorder(registry, options))
                .build();
        connect(resources);
    }

    @Override
    public void clear() {
        try (LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection()) {
            final LettuceCommandsWrapper commandsWrapper = connection.sync();
            commandsWrapper.del(hashKey());
            commandsWrapper.del(sortsetKey());
        } catch (IOException e) {
            Rethrower.throwAs(e);
        }
    }

    @Override
    public void destroy() {

    }

    private void connect(ClientResources resources) {
        List<RedisURI> clusterRedisURIS = options.getClusterRedisURIS();
        if (clusterRedisURIS != null && !clusterRedisURIS.isEmpty()) {
            RedisClusterClient clusterClient = RedisClusterClient.create(resources, clusterRedisURIS);
            this.connectionFactory = new DefaultLettuceConnectionFactory(clusterClient, BoundedPoolConfig.create());
        } else {
            this.connectionFactory = new DefaultLettuceConnectionFactory(RedisClient.create(resources, options.getRedisURI()), BoundedPoolConfig.create());
        }
    }

    private class ExpireTimeTask implements TimerTask {

        @Override
        public void run(Timeout timeout) throws Exception {
            LettuceConnectionFactory.LettuceConnection connection = connectionFactory.getConnection();
            LettuceCommandsWrapper commandsWrapper = connection.sync();
            if (commandsWrapper.exists(sortsetKey()) == 0L) {
                clear();
            }
            List keys = commandsWrapper.zrangebyscore(sortsetKey(), Range.create(0, System.nanoTime()), Limit.from(100L));
            if (keys != null && keys.size() > 0) {
                removeAll(keys);
            }
            LocalDateTime tomorrow = LocalDateTime.now().withNano(0).withSecond(0).withMinute(0).withHour(3).plusDays(1L);
            Instant instant = tomorrow.atZone(ZoneOffset.systemDefault()).toInstant();

            if (commandsWrapper.ttl(hashKey()) == -1) {
                commandsWrapper.expireat(hashKey(), Date.from(instant));
            }
            if (commandsWrapper.ttl(sortsetKey()) == -1) {
                commandsWrapper.expireat(sortsetKey(), Date.from(instant));
            }
            timer.newTimeout(this, 1L, TimeUnit.SECONDS);
        }
    }

    public static class ValueWrapper<V> {
        private final V value;
        private final long expireAt;

        public ValueWrapper(V value, long expireAt) {
            this.value = value;
            this.expireAt = expireAt;
        }

        public V getValue() {
            return value;
        }

        public long getExpireAt() {
            return expireAt;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ValueWrapper<?> that = (ValueWrapper<?>) o;
            return expireAt == that.expireAt &&
                    Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value, expireAt);
        }
    }

}

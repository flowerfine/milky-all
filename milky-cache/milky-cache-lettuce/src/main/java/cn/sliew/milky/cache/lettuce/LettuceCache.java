package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.Cache;
import cn.sliew.milky.cache.CacheLoader;
import cn.sliew.milky.common.log.Logger;
import cn.sliew.milky.common.log.LoggerFactory;
import cn.sliew.milky.common.util.StringUtils;
import io.lettuce.core.Limit;
import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micrometer.core.instrument.MeterRegistry;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
     * 一个小时间轮，用来定时处理sortset和hash中的过期对象。
     */
    private final HashedWheelTimer timer;

    private final StatefulRedisConnection connection;

    private final LettuceCacheOptions<K, V> options;

    public LettuceCache(LettuceCacheOptions<K, V> options) {
        this.options = checkNotNull(options, "options can't be null");

        RedisURI redisURI = RedisURI.builder()
                .withHost(options.getHost())
                .withPort(options.getPort())
                .withDatabase(options.getDatabase())
                .withTimeout(Duration.ofMillis(options.getTimeout()))
                .build();
        if (StringUtils.isNotBlank(options.getPassword())) {
            redisURI.setPassword(options.getPassword());
        }
        this.connection = RedisClient.create(redisURI).connect(ProtostuffCodec.INSTANCE);
        this.timer = new HashedWheelTimer(1, TimeUnit.SECONDS, 64);
        this.timer.newTimeout(new ExpireTimeTask(timer, this, connection), 1L, TimeUnit.SECONDS);
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
        RedisCommands commands = connection.sync();
        ValueWrapper<V> valueWrapper = (ValueWrapper<V>) commands.hget(hashKey(), key);
        if (valueWrapper == null) {
            return null;
        }
        if (valueWrapper.getExpireAt() <= System.nanoTime()) {
            remove(key);
            return null;
        }
        return valueWrapper.getValue();
    }

    @Override
    public boolean containsKey(K key) {
        RedisCommands commands = connection.sync();
        if (commands.hexists(hashKey(), key)) {
            ValueWrapper<V> valueWrapper = (ValueWrapper<V>) commands.hget(hashKey(), key);
            if (valueWrapper.getExpireAt() <= System.nanoTime()) {
                remove(key);
                return false;
            }
            return valueWrapper.getValue() != null;
        }
        return false;
    }

    @Override
    public V computeIfAbsent(K key, CacheLoader<K, V> loader, Duration expire) {
        V value = get(key);
        if (value == null) {
            synchronized (connection) {
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
        RedisCommands commands = connection.sync();
        commands.zadd(sortsetKey(), Long.MAX_VALUE, value);
        commands.hset(hashKey(), key, new ValueWrapper<>(value, Long.MAX_VALUE));
    }

    @Override
    public void put(K key, V value, Duration expire) {
        RedisCommands commands = connection.sync();
        long expireAt = expire.toNanos() + System.nanoTime();
        commands.zadd(sortsetKey(), expireAt, value);
        commands.hset(hashKey(), key, new ValueWrapper<>(value, expireAt));
    }

    @Override
    public void remove(K key) {
        RedisCommands commands = connection.sync();
        commands.hdel(hashKey(), key);
        commands.zrem(sortsetKey(), key);
    }

    @Override
    public void removeAll(Iterable<K> keys) {
        RedisCommands commands = connection.sync();
        commands.hdel(hashKey(), keys);
        commands.zrem(sortsetKey(), keys);
    }

    @Override
    public long size() {
        RedisCommands commands = connection.sync();
        return commands.zcard(sortsetKey());
    }

    @Override
    public boolean supportNullKey() {
        return false;
    }

    @Override
    public boolean supportNullValue() {
        return false;
    }

    /**
     * fixme hkeys hscan
     * todo 考虑使用sortset的key来搞。zscan
     */
    @Override
    public Iterator<K> keyIterator() {
        RedisCommands commands = connection.sync();
        return (Iterator<K>) commands.hkeys(hashKey()).iterator();
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

    }

    @Override
    public void clear() {
        RedisCommands commands = connection.sync();
        commands.del(hashKey());
        commands.del(sortsetKey());
    }

    @Override
    public void destroy() {
        connection.close();
    }

    public static class ExpireTimeTask implements TimerTask {

        private final HashedWheelTimer timer;
        private final LettuceCache cache;
        private final StatefulRedisConnection connection;

        public ExpireTimeTask(HashedWheelTimer timer, LettuceCache cache, StatefulRedisConnection connection) {
            this.timer = timer;
            this.cache = cache;
            this.connection = connection;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            RedisCommands commands = connection.sync();
            if (commands.exists(cache.sortsetKey()) == 0L) {
                cache.clear();
            }
            List keys = commands.zrangebyscore(cache.sortsetKey(), Range.create(0, System.nanoTime()), Limit.from(100L));
            if (keys != null && keys.size() > 0) {
                cache.removeAll(keys);
            }
            LocalDateTime tomorrow = LocalDateTime.now().withNano(0).withSecond(0).withMinute(0).withHour(3).plusDays(1L);
            Instant instant = tomorrow.atZone(ZoneOffset.systemDefault()).toInstant();

            if (commands.ttl(cache.hashKey()) == -1) {
                commands.expireat(cache.hashKey(), Date.from(instant));
            }
            if (commands.ttl(cache.sortsetKey()) == -1) {
                commands.expireat(cache.sortsetKey(), Date.from(instant));
            }

            this.timer.newTimeout(this, 1L, TimeUnit.SECONDS);
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

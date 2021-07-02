package cn.sliew.milky.cache.lettuce;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisHashCommands;
import io.lettuce.core.api.sync.RedisKeyCommands;
import io.lettuce.core.api.sync.RedisSortedSetCommands;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.output.KeyStreamingChannel;
import io.lettuce.core.output.KeyValueStreamingChannel;
import io.lettuce.core.output.ScoredValueStreamingChannel;
import io.lettuce.core.output.ValueStreamingChannel;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LettuceCommandsWrapper<K, V> implements RedisKeyCommands<K, V>, RedisHashCommands<K, V>, RedisSortedSetCommands<K, V> {

    private final StatefulRedisConnection connection;
    private final StatefulRedisClusterConnection clusterConnection;

    public LettuceCommandsWrapper(StatefulRedisConnection connection, StatefulRedisClusterConnection clusterConnection) {
        this.connection = connection;
        this.clusterConnection = clusterConnection;
    }

    private boolean isClusterAware() {
        return this.clusterConnection != null;
    }

    private RedisKeyCommands<K, V> keysCommands() {
        if (isClusterAware()) {
            return this.clusterConnection.sync();
        } else {
            return this.connection.sync();
        }
    }

    private RedisHashCommands<K, V> hashCommands() {
        if (isClusterAware()) {
            return this.clusterConnection.sync();
        } else {
            return this.connection.sync();
        }
    }

    private RedisSortedSetCommands<K, V> sortedSetCommands() {
        if (isClusterAware()) {
            return this.clusterConnection.sync();
        } else {
            return this.connection.sync();
        }
    }

    @Override
    public Boolean copy(K k, K k1) {
        return keysCommands().copy(k, k1);
    }

    @Override
    public Boolean copy(K k, K k1, CopyArgs copyArgs) {
        return keysCommands().copy(k, k1, copyArgs);
    }

    @Override
    public Long del(K... ks) {
        return keysCommands().del(ks);
    }

    @Override
    public Long unlink(K... ks) {
        return keysCommands().unlink(ks);
    }

    @Override
    public byte[] dump(K k) {
        return keysCommands().dump(k);
    }

    @Override
    public Long exists(K... ks) {
        return keysCommands().exists(ks);
    }

    @Override
    public Boolean expire(K k, long l) {
        return keysCommands().expire(k, l);
    }

    @Override
    public Boolean expire(K k, Duration duration) {
        return keysCommands().expire(k, duration);
    }

    @Override
    public Boolean expireat(K k, long l) {
        return keysCommands().expireat(k, l);
    }

    @Override
    public Boolean expireat(K k, Date date) {
        return keysCommands().expireat(k, date);
    }

    @Override
    public Boolean expireat(K k, Instant instant) {
        return keysCommands().expireat(k, instant);
    }

    @Override
    public List<K> keys(K k) {
        return keysCommands().keys(k);
    }

    @Override
    public Long keys(KeyStreamingChannel<K> keyStreamingChannel, K k) {
        return keysCommands().keys(keyStreamingChannel, k);
    }

    @Override
    public String migrate(String s, int i, K k, int i1, long l) {
        return keysCommands().migrate(s, i, k, i1, l);
    }

    @Override
    public String migrate(String s, int i, int i1, long l, MigrateArgs<K> migrateArgs) {
        return keysCommands().migrate(s, i, i1, l, migrateArgs);
    }

    @Override
    public Boolean move(K k, int i) {
        return keysCommands().move(k, i);
    }

    @Override
    public String objectEncoding(K k) {
        return keysCommands().objectEncoding(k);
    }

    @Override
    public Long objectFreq(K k) {
        return keysCommands().objectFreq(k);
    }

    @Override
    public Long objectIdletime(K k) {
        return keysCommands().objectIdletime(k);
    }

    @Override
    public Long objectRefcount(K k) {
        return keysCommands().objectRefcount(k);
    }

    @Override
    public Boolean persist(K k) {
        return keysCommands().persist(k);
    }

    @Override
    public Boolean pexpire(K k, long l) {
        return keysCommands().pexpire(k, l);
    }

    @Override
    public Boolean pexpire(K k, Duration duration) {
        return keysCommands().pexpire(k, duration);
    }

    @Override
    public Boolean pexpireat(K k, long l) {
        return keysCommands().pexpireat(k, l);
    }

    @Override
    public Boolean pexpireat(K k, Date date) {
        return keysCommands().pexpireat(k, date);
    }

    @Override
    public Boolean pexpireat(K k, Instant instant) {
        return keysCommands().pexpireat(k, instant);
    }

    @Override
    public Long pttl(K k) {
        return keysCommands().pttl(k);
    }

    @Override
    public K randomkey() {
        return keysCommands().randomkey();
    }

    @Override
    public String rename(K k, K k1) {
        return keysCommands().rename(k, k1);
    }

    @Override
    public Boolean renamenx(K k, K k1) {
        return keysCommands().renamenx(k, k1);
    }

    @Override
    public String restore(K k, long l, byte[] bytes) {
        return keysCommands().restore(k, l, bytes);
    }

    @Override
    public String restore(K k, byte[] bytes, RestoreArgs restoreArgs) {
        return keysCommands().restore(k, bytes, restoreArgs);
    }

    @Override
    public List<V> sort(K k) {
        return keysCommands().sort(k);
    }

    @Override
    public Long sort(ValueStreamingChannel<V> valueStreamingChannel, K k) {
        return keysCommands().sort(valueStreamingChannel, k);
    }

    @Override
    public List<V> sort(K k, SortArgs sortArgs) {
        return keysCommands().sort(k, sortArgs);
    }

    @Override
    public Long sort(ValueStreamingChannel<V> valueStreamingChannel, K k, SortArgs sortArgs) {
        return keysCommands().sort(valueStreamingChannel, k, sortArgs);
    }

    @Override
    public Long sortStore(K k, SortArgs sortArgs, K k1) {
        return keysCommands().sortStore(k, sortArgs, k1);
    }

    @Override
    public Long touch(K... ks) {
        return keysCommands().touch(ks);
    }

    @Override
    public Long ttl(K k) {
        return keysCommands().ttl(k);
    }

    @Override
    public String type(K k) {
        return keysCommands().type(k);
    }

    @Override
    public KeyScanCursor<K> scan() {
        return keysCommands().scan();
    }

    @Override
    public KeyScanCursor<K> scan(ScanArgs scanArgs) {
        return keysCommands().scan(scanArgs);
    }

    @Override
    public KeyScanCursor<K> scan(ScanCursor scanCursor, ScanArgs scanArgs) {
        return keysCommands().scan(scanCursor, scanArgs);
    }

    @Override
    public KeyScanCursor<K> scan(ScanCursor scanCursor) {
        return keysCommands().scan(scanCursor);
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<K> keyStreamingChannel) {
        return keysCommands().scan(keyStreamingChannel);
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<K> keyStreamingChannel, ScanArgs scanArgs) {
        return keysCommands().scan(keyStreamingChannel, scanArgs);
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<K> keyStreamingChannel, ScanCursor scanCursor, ScanArgs scanArgs) {
        return keysCommands().scan(keyStreamingChannel, scanCursor, scanArgs);
    }

    @Override
    public StreamScanCursor scan(KeyStreamingChannel<K> keyStreamingChannel, ScanCursor scanCursor) {
        return keysCommands().scan(keyStreamingChannel, scanCursor);
    }

    @Override
    public Long hdel(K k, K... ks) {
        return hashCommands().hdel(k, ks);
    }

    @Override
    public Boolean hexists(K k, K k1) {
        return hashCommands().hexists(k, k1);
    }

    @Override
    public V hget(K k, K k1) {
        return hashCommands().hget(k, k1);
    }

    @Override
    public Long hincrby(K k, K k1, long l) {
        return hashCommands().hincrby(k, k1, l);
    }

    @Override
    public Double hincrbyfloat(K k, K k1, double v) {
        return hashCommands().hincrbyfloat(k, k1, v);
    }

    @Override
    public Map<K, V> hgetall(K k) {
        return hashCommands().hgetall(k);
    }

    @Override
    public Long hgetall(KeyValueStreamingChannel<K, V> keyValueStreamingChannel, K k) {
        return hashCommands().hgetall(keyValueStreamingChannel, k);
    }

    @Override
    public List<K> hkeys(K k) {
        return hashCommands().hkeys(k);
    }

    @Override
    public Long hkeys(KeyStreamingChannel<K> keyStreamingChannel, K k) {
        return hashCommands().hkeys(keyStreamingChannel, k);
    }

    @Override
    public Long hlen(K k) {
        return hashCommands().hlen(k);
    }

    @Override
    public List<KeyValue<K, V>> hmget(K k, K... ks) {
        return hashCommands().hmget(k, ks);
    }

    @Override
    public Long hmget(KeyValueStreamingChannel<K, V> keyValueStreamingChannel, K k, K... ks) {
        return hashCommands().hmget(keyValueStreamingChannel, k, ks);
    }

    @Override
    public String hmset(K k, Map<K, V> map) {
        return hashCommands().hmset(k, map);
    }

    @Override
    public K hrandfield(K k) {
        return hashCommands().hrandfield(k);
    }

    @Override
    public List<K> hrandfield(K k, long l) {
        return hashCommands().hrandfield(k, l);
    }

    @Override
    public KeyValue<K, V> hrandfieldWithvalues(K k) {
        return hashCommands().hrandfieldWithvalues(k);
    }

    @Override
    public List<KeyValue<K, V>> hrandfieldWithvalues(K k, long l) {
        return hashCommands().hrandfieldWithvalues(k, l);
    }

    @Override
    public MapScanCursor<K, V> hscan(K k) {
        return hashCommands().hscan(k);
    }

    @Override
    public MapScanCursor<K, V> hscan(K k, ScanArgs scanArgs) {
        return hashCommands().hscan(k, scanArgs);
    }

    @Override
    public MapScanCursor<K, V> hscan(K k, ScanCursor scanCursor, ScanArgs scanArgs) {
        return hashCommands().hscan(k, scanCursor, scanArgs);
    }

    @Override
    public MapScanCursor<K, V> hscan(K k, ScanCursor scanCursor) {
        return hashCommands().hscan(k, scanCursor);
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<K, V> keyValueStreamingChannel, K k) {
        return hashCommands().hscan(keyValueStreamingChannel, k);
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<K, V> keyValueStreamingChannel, K k, ScanArgs scanArgs) {
        return hashCommands().hscan(keyValueStreamingChannel, k, scanArgs);
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<K, V> keyValueStreamingChannel, K k, ScanCursor scanCursor, ScanArgs scanArgs) {
        return hashCommands().hscan(keyValueStreamingChannel, k, scanCursor, scanArgs);
    }

    @Override
    public StreamScanCursor hscan(KeyValueStreamingChannel<K, V> keyValueStreamingChannel, K k, ScanCursor scanCursor) {
        return hashCommands().hscan(keyValueStreamingChannel, k, scanCursor);
    }

    @Override
    public Boolean hset(K k, K k1, V v) {
        return hashCommands().hset(k, k1, v);
    }

    @Override
    public Long hset(K k, Map<K, V> map) {
        return hashCommands().hset(k, map);
    }

    @Override
    public Boolean hsetnx(K k, K k1, V v) {
        return hashCommands().hsetnx(k, k1, v);
    }

    @Override
    public Long hstrlen(K k, K k1) {
        return hashCommands().hstrlen(k, k1);
    }

    @Override
    public List<V> hvals(K k) {
        return hashCommands().hvals(k);
    }

    @Override
    public Long hvals(ValueStreamingChannel<V> valueStreamingChannel, K k) {
        return hashCommands().hvals(valueStreamingChannel, k);
    }

    @Override
    public KeyValue<K, ScoredValue<V>> bzpopmin(long l, K... ks) {
        return sortedSetCommands().bzpopmin(l, ks);
    }

    @Override
    public KeyValue<K, ScoredValue<V>> bzpopmax(long l, K... ks) {
        return sortedSetCommands().bzpopmax(l, ks);
    }

    @Override
    public Long zadd(K k, double v, V v1) {
        return sortedSetCommands().zadd(k, v, v1);
    }

    @Override
    public Long zadd(K k, Object... objects) {
        return sortedSetCommands().zadd(k, objects);
    }

    @Override
    public Long zadd(K k, ScoredValue<V>... scoredValues) {
        return sortedSetCommands().zadd(k, scoredValues);
    }

    @Override
    public Long zadd(K k, ZAddArgs zAddArgs, double v, V v1) {
        return sortedSetCommands().zadd(k, zAddArgs, v, v1);
    }

    @Override
    public Long zadd(K k, ZAddArgs zAddArgs, Object... objects) {
        return sortedSetCommands().zadd(k, zAddArgs, objects);
    }

    @Override
    public Long zadd(K k, ZAddArgs zAddArgs, ScoredValue<V>... scoredValues) {
        return sortedSetCommands().zadd(k, zAddArgs, scoredValues);
    }

    @Override
    public Double zaddincr(K k, double v, V v1) {
        return sortedSetCommands().zaddincr(k, v, v1);
    }

    @Override
    public Double zaddincr(K k, ZAddArgs zAddArgs, double v, V v1) {
        return sortedSetCommands().zaddincr(k, zAddArgs, v, v1);
    }

    @Override
    public Long zcard(K k) {
        return sortedSetCommands().zcard(k);
    }

    @Override
    public Long zcount(K k, double v, double v1) {
        return sortedSetCommands().zcount(k, v, v1);
    }

    @Override
    public Long zcount(K k, String s, String s1) {
        return sortedSetCommands().zcount(k, s, s1);
    }

    @Override
    public Long zcount(K k, Range<? extends Number> range) {
        return sortedSetCommands().zcount(k, range);
    }

    @Override
    public List<V> zdiff(K... ks) {
        return sortedSetCommands().zdiff(ks);
    }

    @Override
    public Long zdiffstore(K k, K... ks) {
        return sortedSetCommands().zdiffstore(k, ks);
    }

    @Override
    public List<ScoredValue<V>> zdiffWithScores(K... ks) {
        return sortedSetCommands().zdiffWithScores(ks);
    }

    @Override
    public Double zincrby(K k, double v, V v1) {
        return sortedSetCommands().zincrby(k, v, v1);
    }

    @Override
    public List<V> zinter(K... ks) {
        return sortedSetCommands().zinter(ks);
    }

    @Override
    public List<V> zinter(ZAggregateArgs zAggregateArgs, K... ks) {
        return sortedSetCommands().zinter(zAggregateArgs, ks);
    }

    @Override
    public List<ScoredValue<V>> zinterWithScores(ZAggregateArgs zAggregateArgs, K... ks) {
        return sortedSetCommands().zinterWithScores(zAggregateArgs, ks);
    }

    @Override
    public List<ScoredValue<V>> zinterWithScores(K... ks) {
        return sortedSetCommands().zinterWithScores(ks);
    }

    @Override
    public Long zinterstore(K k, K... ks) {
        return sortedSetCommands().zinterstore(k, ks);
    }

    @Override
    public Long zinterstore(K k, ZStoreArgs zStoreArgs, K... ks) {
        return sortedSetCommands().zinterstore(k, zStoreArgs, ks);
    }

    @Override
    public Long zlexcount(K k, String s, String s1) {
        return sortedSetCommands().zlexcount(k, s, s1);
    }

    @Override
    public Long zlexcount(K k, Range<? extends V> range) {
        return sortedSetCommands().zlexcount(k, range);
    }

    @Override
    public List<Double> zmscore(K k, V... vs) {
        return sortedSetCommands().zmscore(k, vs);
    }

    @Override
    public ScoredValue<V> zpopmin(K k) {
        return sortedSetCommands().zpopmin(k);
    }

    @Override
    public List<ScoredValue<V>> zpopmin(K k, long l) {
        return sortedSetCommands().zpopmin(k, l);
    }

    @Override
    public ScoredValue<V> zpopmax(K k) {
        return sortedSetCommands().zpopmax(k);
    }

    @Override
    public List<ScoredValue<V>> zpopmax(K k, long l) {
        return sortedSetCommands().zpopmax(k, l);
    }

    @Override
    public V zrandmember(K k) {
        return sortedSetCommands().zrandmember(k);
    }

    @Override
    public List<V> zrandmember(K k, long l) {
        return sortedSetCommands().zrandmember(k, l);
    }

    @Override
    public ScoredValue<V> zrandmemberWithScores(K k) {
        return sortedSetCommands().zrandmemberWithScores(k);
    }

    @Override
    public List<ScoredValue<V>> zrandmemberWithScores(K k, long l) {
        return sortedSetCommands().zrandmemberWithScores(k, l);
    }

    @Override
    public List<V> zrange(K k, long l, long l1) {
        return sortedSetCommands().zrange(k, l, l1);
    }

    @Override
    public Long zrange(ValueStreamingChannel<V> valueStreamingChannel, K k, long l, long l1) {
        return sortedSetCommands().zrange(valueStreamingChannel, k, l, l1);
    }

    @Override
    public List<ScoredValue<V>> zrangeWithScores(K k, long l, long l1) {
        return sortedSetCommands().zrangeWithScores(k, l, l1);
    }

    @Override
    public Long zrangeWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, long l, long l1) {
        return sortedSetCommands().zrangeWithScores(scoredValueStreamingChannel, k, l, l1);
    }

    @Override
    public List<V> zrangebylex(K k, String s, String s1) {
        return sortedSetCommands().zrangebylex(k, s, s1);
    }

    @Override
    public List<V> zrangebylex(K k, Range<? extends V> range) {
        return sortedSetCommands().zrangebylex(k, range);
    }

    @Override
    public List<V> zrangebylex(K k, String s, String s1, long l, long l1) {
        return sortedSetCommands().zrangebylex(k, s, s1, l, l1);
    }

    @Override
    public List<V> zrangebylex(K k, Range<? extends V> range, Limit limit) {
        return sortedSetCommands().zrangebylex(k, range, limit);
    }

    @Override
    public List<V> zrangebyscore(K k, double v, double v1) {
        return sortedSetCommands().zrangebyscore(k, v, v1);
    }

    @Override
    public List<V> zrangebyscore(K k, String s, String s1) {
        return sortedSetCommands().zrangebyscore(k, s, s1);
    }

    @Override
    public List<V> zrangebyscore(K k, Range<? extends Number> range) {
        return sortedSetCommands().zrangebyscore(k, range);
    }

    @Override
    public List<V> zrangebyscore(K k, double v, double v1, long l, long l1) {
        return sortedSetCommands().zrangebyscore(k, v, v1, l, l1);
    }

    @Override
    public List<V> zrangebyscore(K k, String s, String s1, long l, long l1) {
        return sortedSetCommands().zrangebyscore(k, s, s1, l, l1);
    }

    @Override
    public List<V> zrangebyscore(K k, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrangebyscore(k, range, limit);
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, double v, double v1) {
        return sortedSetCommands().zrangebyscore(valueStreamingChannel, k, v, v1);
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, String s, String s1) {
        return sortedSetCommands().zrangebyscore(valueStreamingChannel, k, s, s1);
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, Range<? extends Number> range) {
        return sortedSetCommands().zrangebyscore(valueStreamingChannel, k, range);
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, double v, double v1, long l, long l1) {
        return sortedSetCommands().zrangebyscore(valueStreamingChannel, k, v, v1, l, l1);
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, String s, String s1, long l, long l1) {
        return sortedSetCommands().zrangebyscore(valueStreamingChannel, k, s, s1, l, l1);
    }

    @Override
    public Long zrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrangebyscore(valueStreamingChannel, k, range, limit);
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K k, double v, double v1) {
        return sortedSetCommands().zrangebyscoreWithScores(k, v, v1);
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K k, String s, String s1) {
        return sortedSetCommands().zrangebyscoreWithScores(k, s, s1);
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K k, Range<? extends Number> range) {
        return sortedSetCommands().zrangebyscoreWithScores(k, range);
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K k, double v, double v1, long l, long l1) {
        return sortedSetCommands().zrangebyscoreWithScores(k, v, v1, l, l1);
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K k, String s, String s1, long l, long l1) {
        return sortedSetCommands().zrangebyscoreWithScores(k, s, s1, l, l1);
    }

    @Override
    public List<ScoredValue<V>> zrangebyscoreWithScores(K k, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrangebyscoreWithScores(k, range, limit);
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, double v, double v1) {
        return sortedSetCommands().zrangebyscoreWithScores(scoredValueStreamingChannel, k, v, v1);
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, String s, String s1) {
        return sortedSetCommands().zrangebyscoreWithScores(scoredValueStreamingChannel, k, s, s1);
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, Range<? extends Number> range) {
        return sortedSetCommands().zrangebyscoreWithScores(scoredValueStreamingChannel, k, range);
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, double v, double v1, long l, long l1) {
        return sortedSetCommands().zrangebyscoreWithScores(scoredValueStreamingChannel, k, v, v1, l, l1);
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, String s, String s1, long l, long l1) {
        return sortedSetCommands().zrangebyscoreWithScores(scoredValueStreamingChannel, k, s, s1, l, l1);
    }

    @Override
    public Long zrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrangebyscoreWithScores(scoredValueStreamingChannel, k, range, limit);
    }

    @Override
    public Long zrangestorebylex(K k, K k1, Range<? extends V> range, Limit limit) {
        return sortedSetCommands().zrangestorebylex(k, k1, range, limit);
    }

    @Override
    public Long zrangestorebyscore(K k, K k1, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrangestorebyscore(k, k1, range, limit);
    }

    @Override
    public Long zrank(K k, V v) {
        return sortedSetCommands().zrank(k, v);
    }

    @Override
    public Long zrem(K k, V... vs) {
        return sortedSetCommands().zrem(k, vs);
    }

    @Override
    public Long zremrangebylex(K k, String s, String s1) {
        return sortedSetCommands().zremrangebylex(k, s, s1);
    }

    @Override
    public Long zremrangebylex(K k, Range<? extends V> range) {
        return sortedSetCommands().zremrangebylex(k, range);
    }

    @Override
    public Long zremrangebyrank(K k, long l, long l1) {
        return sortedSetCommands().zremrangebyrank(k, l, l1);
    }

    @Override
    public Long zremrangebyscore(K k, double v, double v1) {
        return sortedSetCommands().zremrangebyscore(k, v, v1);
    }

    @Override
    public Long zremrangebyscore(K k, String s, String s1) {
        return sortedSetCommands().zremrangebyscore(k, s, s1);
    }

    @Override
    public Long zremrangebyscore(K k, Range<? extends Number> range) {
        return sortedSetCommands().zremrangebyscore(k, range);
    }

    @Override
    public List<V> zrevrange(K k, long l, long l1) {
        return sortedSetCommands().zrevrange(k, l, l1);
    }

    @Override
    public Long zrevrange(ValueStreamingChannel<V> valueStreamingChannel, K k, long l, long l1) {
        return sortedSetCommands().zrevrange(valueStreamingChannel, k, l, l1);
    }

    @Override
    public List<ScoredValue<V>> zrevrangeWithScores(K k, long l, long l1) {
        return sortedSetCommands().zrevrangeWithScores(k, l, l1);
    }

    @Override
    public Long zrevrangeWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, long l, long l1) {
        return sortedSetCommands().zrevrangeWithScores(scoredValueStreamingChannel, k, l, l1);
    }

    @Override
    public List<V> zrevrangebylex(K k, Range<? extends V> range) {
        return sortedSetCommands().zrevrangebylex(k, range);
    }

    @Override
    public List<V> zrevrangebylex(K k, Range<? extends V> range, Limit limit) {
        return sortedSetCommands().zrevrangebylex(k, range, limit);
    }

    @Override
    public List<V> zrevrangebyscore(K k, double v, double v1) {
        return sortedSetCommands().zrevrangebyscore(k, v, v1);
    }

    @Override
    public List<V> zrevrangebyscore(K k, String s, String s1) {
        return sortedSetCommands().zrevrangebyscore(k, s, s1);
    }

    @Override
    public List<V> zrevrangebyscore(K k, Range<? extends Number> range) {
        return sortedSetCommands().zrevrangebyscore(k, range);
    }

    @Override
    public List<V> zrevrangebyscore(K k, double v, double v1, long l, long l1) {
        return sortedSetCommands().zrevrangebyscore(k, v, v1, l, l1);
    }

    @Override
    public List<V> zrevrangebyscore(K k, String s, String s1, long l, long l1) {
        return sortedSetCommands().zrevrangebyscore(k, s, s1, l, l1);
    }

    @Override
    public List<V> zrevrangebyscore(K k, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrevrangebyscore(k, range, limit);
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, double v, double v1) {
        return sortedSetCommands().zrevrangebyscore(valueStreamingChannel, k, v, v1);
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, String s, String s1) {
        return sortedSetCommands().zrevrangebyscore(valueStreamingChannel, k, s, s1);
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, Range<? extends Number> range) {
        return sortedSetCommands().zrevrangebyscore(valueStreamingChannel, k, range);
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, double v, double v1, long l, long l1) {
        return sortedSetCommands().zrevrangebyscore(valueStreamingChannel, k, v, v1, l, l1);
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, String s, String s1, long l, long l1) {
        return sortedSetCommands().zrevrangebyscore(valueStreamingChannel, k, s, s1, l, l1);
    }

    @Override
    public Long zrevrangebyscore(ValueStreamingChannel<V> valueStreamingChannel, K k, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrevrangebyscore(valueStreamingChannel, k, range, limit);
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K k, double v, double v1) {
        return sortedSetCommands().zrevrangebyscoreWithScores(k, v, v1);
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K k, String s, String s1) {
        return sortedSetCommands().zrevrangebyscoreWithScores(k, s, s1);
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K k, Range<? extends Number> range) {
        return sortedSetCommands().zrevrangebyscoreWithScores(k, range);
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K k, double v, double v1, long l, long l1) {
        return sortedSetCommands().zrevrangebyscoreWithScores(k, v, v1, l, l1);
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K k, String s, String s1, long l, long l1) {
        return sortedSetCommands().zrevrangebyscoreWithScores(k, s, s1, l, l1);
    }

    @Override
    public List<ScoredValue<V>> zrevrangebyscoreWithScores(K k, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrevrangebyscoreWithScores(k, range, limit);
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, double v, double v1) {
        return sortedSetCommands().zrevrangebyscoreWithScores(scoredValueStreamingChannel, k, v, v1);
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, String s, String s1) {
        return sortedSetCommands().zrevrangebyscoreWithScores(scoredValueStreamingChannel, k, s, s1);
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, Range<? extends Number> range) {
        return sortedSetCommands().zrevrangebyscoreWithScores(scoredValueStreamingChannel, k, range);
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, double v, double v1, long l, long l1) {
        return sortedSetCommands().zrevrangebyscoreWithScores(scoredValueStreamingChannel, k, v, v1, l, l1);
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, String s, String s1, long l, long l1) {
        return sortedSetCommands().zrevrangebyscoreWithScores(scoredValueStreamingChannel, k, s, s1, l, l1);
    }

    @Override
    public Long zrevrangebyscoreWithScores(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrevrangebyscoreWithScores(scoredValueStreamingChannel, k, range, limit);
    }

    @Override
    public Long zrevrangestorebylex(K k, K k1, Range<? extends V> range, Limit limit) {
        return sortedSetCommands().zrevrangestorebylex(k, k1, range, limit);
    }

    @Override
    public Long zrevrangestorebyscore(K k, K k1, Range<? extends Number> range, Limit limit) {
        return sortedSetCommands().zrevrangestorebyscore(k, k1, range, limit);
    }

    @Override
    public Long zrevrank(K k, V v) {
        return sortedSetCommands().zrevrank(k, v);
    }

    @Override
    public ScoredValueScanCursor<V> zscan(K k) {
        return sortedSetCommands().zscan(k);
    }

    @Override
    public ScoredValueScanCursor<V> zscan(K k, ScanArgs scanArgs) {
        return sortedSetCommands().zscan(k, scanArgs);
    }

    @Override
    public ScoredValueScanCursor<V> zscan(K k, ScanCursor scanCursor, ScanArgs scanArgs) {
        return sortedSetCommands().zscan(k, scanCursor, scanArgs);
    }

    @Override
    public ScoredValueScanCursor<V> zscan(K k, ScanCursor scanCursor) {
        return sortedSetCommands().zscan(k, scanCursor);
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k) {
        return sortedSetCommands().zscan(scoredValueStreamingChannel, k);
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, ScanArgs scanArgs) {
        return sortedSetCommands().zscan(scoredValueStreamingChannel, k, scanArgs);
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, ScanCursor scanCursor, ScanArgs scanArgs) {
        return sortedSetCommands().zscan(scoredValueStreamingChannel, k, scanCursor, scanArgs);
    }

    @Override
    public StreamScanCursor zscan(ScoredValueStreamingChannel<V> scoredValueStreamingChannel, K k, ScanCursor scanCursor) {
        return sortedSetCommands().zscan(scoredValueStreamingChannel, k, scanCursor);
    }

    @Override
    public Double zscore(K k, V v) {
        return sortedSetCommands().zscore(k, v);
    }

    @Override
    public List<V> zunion(K... ks) {
        return sortedSetCommands().zunion(ks);
    }

    @Override
    public List<V> zunion(ZAggregateArgs zAggregateArgs, K... ks) {
        return sortedSetCommands().zunion(zAggregateArgs, ks);
    }

    @Override
    public List<ScoredValue<V>> zunionWithScores(ZAggregateArgs zAggregateArgs, K... ks) {
        return sortedSetCommands().zunionWithScores(zAggregateArgs, ks);
    }

    @Override
    public List<ScoredValue<V>> zunionWithScores(K... ks) {
        return sortedSetCommands().zunionWithScores(ks);
    }

    @Override
    public Long zunionstore(K k, K... ks) {
        return sortedSetCommands().zunionstore(k, ks);
    }

    @Override
    public Long zunionstore(K k, ZStoreArgs zStoreArgs, K... ks) {
        return sortedSetCommands().zunionstore(k, zStoreArgs, ks);
    }
}

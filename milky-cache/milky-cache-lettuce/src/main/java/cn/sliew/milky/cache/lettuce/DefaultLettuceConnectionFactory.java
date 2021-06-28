package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.common.exception.Rethrower;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.support.BoundedPoolConfig;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;

public class DefaultLettuceConnectionFactory implements LettuceConnectionFactory {

    private final GenericObjectPoolConfig poolConfig;

    private GenericObjectPool<StatefulRedisConnection<?, ?>> connectionPool;
    private GenericObjectPool<StatefulRedisClusterConnection<?, ?>> clusterConnectionPool;

    public DefaultLettuceConnectionFactory(RedisClient client, BoundedPoolConfig poolConfig) {
        this.poolConfig = convert(poolConfig);
        this.connectionPool = ConnectionPoolSupport.createGenericObjectPool(() -> client.connect(ProtostuffCodec.INSTANCE), this.poolConfig);
    }

    public DefaultLettuceConnectionFactory(RedisClusterClient clusterClient, BoundedPoolConfig poolConfig) {
        this.poolConfig = convert(poolConfig);
        this.clusterConnectionPool = ConnectionPoolSupport.createGenericObjectPool(() -> clusterClient.connect(ProtostuffCodec.INSTANCE), this.poolConfig);
    }

    @Override
    public LettuceConnection getConnection() {
        try {
            if (connectionPool != null) {
                return new LettuceConnectionWrapper(connectionPool.borrowObject());
            } else {
                return new LettuceConnectionWrapper(clusterConnectionPool.borrowObject());
            }
        } catch (Exception e) {
            Rethrower.throwAs(e);
            // should never reach this
            return null;
        }
    }

    private void release(StatefulRedisConnection connection) {
        if (connectionPool != null) {
            connectionPool.returnObject(connection);
        }
    }

    private void release(StatefulRedisClusterConnection clusterConnection) {
        if (clusterConnectionPool != null) {
            clusterConnectionPool.returnObject(clusterConnection);
        }
    }

    private GenericObjectPoolConfig convert(BoundedPoolConfig poolConfig) {
        GenericObjectPoolConfig target = new GenericObjectPoolConfig();
        if (poolConfig.getMaxTotal() > 0) {
            target.setMaxTotal(poolConfig.getMaxTotal());
        } else {
            target.setMaxTotal(Integer.MAX_VALUE);
        }
        if (poolConfig.getMaxIdle() > 0) {
            target.setMaxIdle(poolConfig.getMaxIdle());
        } else {
            target.setMaxIdle(Integer.MAX_VALUE);
        }
        target.setMinIdle(poolConfig.getMinIdle());
        target.setTestOnBorrow(poolConfig.isTestOnAcquire());
        target.setTestOnCreate(poolConfig.isTestOnCreate());
        target.setTestOnReturn(poolConfig.isTestOnRelease());
        return target;
    }

    private class LettuceConnectionWrapper implements LettuceConnection {

        private StatefulRedisConnection connection;
        private StatefulRedisClusterConnection clusterConnection;

        LettuceConnectionWrapper(StatefulRedisConnection connection) {
            this.connection = connection;
        }

        LettuceConnectionWrapper(StatefulRedisClusterConnection clusterConnection) {
            this.clusterConnection = clusterConnection;
        }

        public LettuceCommandsWrapper sync() {
            return new LettuceCommandsWrapper(this.connection, this.clusterConnection);
        }

        public LettuceAsyncCommandsWrapper async() {
            return null;
        }

        @Override
        public void close() throws IOException {
            if (connection != null) {
                release(connection);
            }
            if (clusterConnection != null) {
                release(clusterConnection);
            }
        }
    }
}

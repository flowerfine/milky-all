package cn.sliew.milky.cache.lettuce;

import cn.sliew.milky.cache.CacheOptions;

import static cn.sliew.milky.common.check.Ensures.checkArgument;
import static cn.sliew.milky.common.check.Ensures.notBlank;

public class LettuceCacheOptions<K, V> extends CacheOptions<K, V> {

    private String host = "localhost";
    private int port = 6379;
    private String password;
    private int database = 0;

    /**
     * millseconds, default 1000ms
     */
    private long timeout = 1000L;

    public LettuceCacheOptions() {
        super();
    }

    /**
     * Sets redis {@code host}.
     *
     * @param host redis host
     * @return LettuceCacheOptions instance
     */
    public LettuceCacheOptions<K, V> host(String host) {
        notBlank(host, "lettuce host can't be empty");
        this.host = host;
        return this;
    }

    public String getHost() {
        return host;
    }

    /**
     * Sets redis {@code port}.
     *
     * @param port redis port
     * @return LettuceCacheOptions instance
     */
    public LettuceCacheOptions<K, V> port(int port) {
        checkArgument(port > 0 && port <= 65535, String.format("lettuce port invalid: %d", port));
        this.port = port;
        return this;
    }

    public int getPort() {
        return port;
    }

    /**
     * Sets redis {@code password}.
     *
     * @param password redis password
     * @return LettuceCacheOptions instance
     */
    public LettuceCacheOptions<K, V> password(String password) {
        notBlank(password, "lettuce password can't be empty");
        this.password = password;
        return this;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Sets redis {@code database}.
     *
     * @param database redis database
     * @return LettuceCacheOptions instance
     */
    public LettuceCacheOptions<K, V> database(int database) {
        checkArgument(database >= 0, String.format("lettuce database invalid: %d", port));
        this.database = database;
        return this;
    }

    public int getDatabase() {
        return database;
    }

    /**
     * Sets redis command {@code timeout}.
     *
     * @param timeout redis timeout
     * @return LettuceCacheOptions instance
     */
    public LettuceCacheOptions<K, V> timeout(long timeout) {
        checkArgument(timeout > 0L, String.format("lettuce timeout invalid: %d", timeout));
        this.timeout = timeout;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }
}

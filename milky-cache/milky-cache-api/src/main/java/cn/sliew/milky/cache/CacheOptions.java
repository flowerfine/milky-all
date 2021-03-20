package cn.sliew.milky.cache;

import static cn.sliew.milky.common.check.Ensures.notBlank;

public class CacheOptions<K, V> {

    /**
     * 缓存的四种模式：stand by，read through，write through，write behind。
     */
    public enum WriteMode {

        /**
         * In write behind mode all data written in map object
         * also written using MapWriter in asynchronous mode.
         */
        WRITE_BEHIND,

        /**
         * In write through mode all write operations for map object
         * are synchronized with MapWriter write operations.
         * If MapWriter throws an error then it will be re-thrown to Map operation caller.
         */
        WRITE_THROUGH
    }

    private String name = "default-cache";

    private CacheLoader<K, V> loader;
    private CacheWriter<K, V> writer;

    private WriteMode writeMode = WriteMode.WRITE_THROUGH;
    private int writeBehindThreads = 1;

    protected CacheOptions() {

    }

    public static <K, V> CacheOptions<K, V> defaults() {
        return new CacheOptions<>();
    }

    /**
     * Sets cache {@code name}.
     *
     * @param name cache name
     * @return MapOptions instance
     */
    public CacheOptions<K, V> name(String name) {
        notBlank(name, "cache name can't be empty");
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    /**
     * Sets {@link CacheWriter} object.
     *
     * @param writer object
     * @return MapOptions instance
     */
    public CacheOptions<K, V> writer(CacheWriter<K, V> writer) {
        this.writer = writer;
        return this;
    }

    public CacheWriter<K, V> getWriter() {
        return writer;
    }

    /**
     * Sets threads amount used in write behind mode.
     * <p>
     * Default is <code>1</code>
     *
     * @param writeBehindThreads - threads amount
     * @return MapOptions instance
     */
    public CacheOptions<K, V> writeBehindThreads(int writeBehindThreads) {
        this.writeBehindThreads = writeBehindThreads;
        return this;
    }

    public int getWriteBehindThreads() {
        return writeBehindThreads;
    }

    /**
     * Sets write mode.
     * <p>
     * Default is <code>{@link WriteMode#WRITE_THROUGH}</code>
     *
     * @param writeMode - write mode
     * @return MapOptions instance
     */
    public CacheOptions<K, V> writeMode(WriteMode writeMode) {
        this.writeMode = writeMode;
        return this;
    }

    public WriteMode getWriteMode() {
        return writeMode;
    }

    /**
     * Sets {@link CacheLoader} object.
     *
     * @param loader object
     * @return MapOptions instance
     */
    public CacheOptions<K, V> loader(CacheLoader<K, V> loader) {
        this.loader = loader;
        return this;
    }

    public CacheLoader<K, V> getLoader() {
        return loader;
    }


}

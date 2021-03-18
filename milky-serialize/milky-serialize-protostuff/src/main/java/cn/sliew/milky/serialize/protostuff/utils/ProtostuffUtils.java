package cn.sliew.milky.serialize.protostuff.utils;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffUtils {

    /**
     * 避免每次序列化都重新申请Buffer空间。
     * LinkedBuffer线程不安全，使用ThreadLocal保证线程安全。
     * 更合适的做法应该是使用池子，因为一个应用的线程折腾个好几百是so easy的。
     * 每个线程都占据一个 {@link LinkedBuffer} 对象加起来就是一笔不小的开支。
     */
    static ThreadLocal<LinkedBuffer> THREAD_BUFFER = ThreadLocal.withInitial(() -> LinkedBuffer.allocate());

    /**
     * 缓存Schema
     */
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    private ProtostuffUtils() {
        throw new IllegalStateException("can't do this!");
    }

    /**
     * 序列化方法，把指定对象序列化成字节数组
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = getSchema(clazz);
        LinkedBuffer buffer = THREAD_BUFFER.get();
        byte[] data;
        try {
            data = ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }

        return data;
    }

    /**
     * 反序列化方法，将字节数组反序列化成指定Class类型
     */
    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        Schema<T> schema = getSchema(clazz);
        T obj = schema.newMessage();
        ProtobufIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) schemaCache.get(clazz);
        if (Objects.isNull(schema)) {
            //RuntimeSchema.getSchema()是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }

        return schema;
    }
}
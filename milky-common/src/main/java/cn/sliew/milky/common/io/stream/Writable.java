package cn.sliew.milky.common.io.stream;

import java.io.IOException;

/**
 * todo 如果使用它作为transport层的传输对象，则无法嵌入自定义的协议
 */
public interface Writable {

    /**
     * Write this object's fields to a {@linkplain StreamOutput}.
     */
    void writeTo(StreamOutput out) throws IOException;

    @FunctionalInterface
    interface Writer<V> {

        /**
         * Write {@code V}-type {@code value} to the {@code out}put stream.
         *
         * @param out   Output to write the {@code value} too
         * @param value The value to add
         */
        void write(StreamOutput out, V value) throws IOException;

    }
}

package cn.sliew.milky.common.io.stream;

import java.io.IOException;

public interface Writeable {

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

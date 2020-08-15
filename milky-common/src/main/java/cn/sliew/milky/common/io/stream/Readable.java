package cn.sliew.milky.common.io.stream;

import java.io.IOException;

public interface Readable {

    /**
     * Set this object's fields from a {@linkplain StreamInput}.
     */
    void readFrom(StreamInput in) throws IOException;

    /**
     * Reference to a method that can read some object from a stream. By convention this is a constructor that takes
     * {@linkplain StreamInput} as an argument for most classes and a static method for things like enums. Returning null from one of these
     * is always wrong - for that we use methods like {@link StreamInput#readOptionalWriteable(Reader)}.
     * <p>
     * As most classes will implement this via a constructor (or a static method in the case of enumerations), it's something that should
     * look like:
     * <pre><code>
     * public MyClass(final StreamInput in) throws IOException {
     *     this.someValue = in.readVInt();
     *     this.someMap = in.readMapOfLists(StreamInput::readString, StreamInput::readString);
     * }
     * </code></pre>
     */
    @FunctionalInterface
    interface Reader<V> {

        /**
         * Read {@code V}-type value from a stream.
         *
         * @param in Input to read the value from
         */
        V read(StreamInput in) throws IOException;

    }
}

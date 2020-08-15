package cn.sliew.milky.common.io.stream;

import java.io.InputStream;

/**
 * A stream from this node to another node. Technically, it can also be streamed to a byte array but that is mostly for testing.
 * <p>
 * This class's methods are optimized so you can put the methods that read and write a class next to each other and you can scan them
 * visually for differences. That means that most variables should be read and written in a single line so even large objects fit both
 * reading and writing on the screen. It also means that the methods on this class are named very similarly to {@link StreamOutput}. Finally
 * it means that the "barrier to entry" for adding new methods to this class is relatively low even though it is a shared class with code
 * everywhere. That being said, this class deals primarily with {@code List}s rather than Arrays. For the most part calls should adapt to
 * lists, either by storing {@code List}s internally or just converting to and from a {@code List} when calling. This comment is repeated
 * on {@link StreamInput}.
 */
public abstract class StreamInput extends InputStream {

}

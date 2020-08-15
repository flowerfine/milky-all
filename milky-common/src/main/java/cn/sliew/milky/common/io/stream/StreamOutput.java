package cn.sliew.milky.common.io.stream;

import java.io.OutputStream;

/**
 * A stream from another node to this node. Technically, it can also be streamed from a byte array but that is mostly for testing.
 * <p>
 * This class's methods are optimized so you can put the methods that read and write a class next to each other and you can scan them
 * visually for differences. That means that most variables should be read and written in a single line so even large objects fit both
 * reading and writing on the screen. It also means that the methods on this class are named very similarly to {@link StreamInput}. Finally
 * it means that the "barrier to entry" for adding new methods to this class is relatively low even though it is a shared class with code
 * everywhere. That being said, this class deals primarily with {@code List}s rather than Arrays. For the most part calls should adapt to
 * lists, either by storing {@code List}s internally or just converting to and from a {@code List} when calling. This comment is repeated
 * on {@link StreamInput}.
 */
public abstract class StreamOutput extends OutputStream {

}

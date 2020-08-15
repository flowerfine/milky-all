package cn.sliew.milky.common.io.stream;

/**
 * Implementers can be written to a {@linkplain StreamOutput} and read from a {@linkplain StreamInput}. If the implementer also
 * implements equals and hashCode then a copy made by serializing and deserializing must be equal and have the same hashCode.
 * It isn't required that such a copy be entirely unchanged.
 */
public interface Streamable extends Writeable, Readable {

}

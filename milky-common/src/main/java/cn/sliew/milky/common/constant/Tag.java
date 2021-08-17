package cn.sliew.milky.common.constant;

/**
 * An tag which allows to store a string tag value.
 */
public class Tag extends AbstractConstant<Tag> {

    private static final ConstantPool<Tag> pool = new ConstantPool<Tag>() {
        @Override
        protected Tag newConstant(int id, String name) {
            return new Tag(id, name);
        }
    };

    /**
     * Return the string tag value
     */
    public String tag() {
        return name();
    }

    /**
     * Returns the singleton instance of the {@link Tag} which has the specified {@code name}.
     */
    public static Tag valueOf(String name) {
        return pool.valueOf(name);
    }

    /**
     * Returns {@code true} if a {@link Tag} exists for the given {@code name}.
     */
    public static boolean exists(String name) {
        return pool.exists(name);
    }

    /**
     * Creates a new {@link Tag} for the given {@code name} or fail with an
     * {@link IllegalArgumentException} if a {@link Tag} for the given {@code name} exists.
     */
    public static Tag newInstance(String name) {
        return pool.newInstance(name);
    }

    private Tag(int id, String name) {
        super(id, name);
    }
}

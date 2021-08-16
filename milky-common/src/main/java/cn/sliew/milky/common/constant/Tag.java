package cn.sliew.milky.common.constant;

/**
 * An tag which allows to store a string tag value.
 */
public class Tag extends AbstractConstant<Tag> {

    private static final ConstantPool<Tag> pool = new ConstantPool<Tag>() {
        @Override
        protected Tag newConstant(int id, String name) {
            return new AttributeKey<>(id, name);
        }
    };

    /**
     * Return the string tag value
     */
    public String tag() {
        return name();
    }

    private Tag(int id, String name) {
        super(id, name);
    }
}

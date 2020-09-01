package cn.sliew.milky.common.constant;

import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link Constant}的基类实现。
 */
public abstract class AbstractConstant<T extends AbstractConstant<T>> implements Constant<T> {

    /**
     * 这里的四个变量都是final，代表不可变。但是uniqueIdGenerator是一个实例变量，是可以改变的。
     * 但是从构造器可以看出，uniqueIdGenerator的主要作用是用来获取uniquifier的。
     * 所以这种使用final变量的目的还是为了实现不可变的对象以保证线程安全。
     * 因为在{@link Constant}中的注释说明{@link Constant}的创建和管理由
     * {@link ConstantPool}实现，所以要理解如何实现的线程安全，还需要结合
     * {@link ConstantPool}实现细节。
     */
    private static final AtomicLong uniqueIdGenerator = new AtomicLong();
    private final int id;
    private final String name;
    private final long uniquifier;

    /**
     * Creates a new instance.
     */
    protected AbstractConstant(int id, String name) {
        this.id = id;
        this.name = name;
        this.uniquifier = uniqueIdGenerator.getAndIncrement();
    }

    @Override
    public final String name() {
        return name;
    }

    @Override
    public final int id() {
        return id;
    }

    @Override
    public final String toString() {
        return name();
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int compareTo(T o) {
        if (this == o) {
            return 0;
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        AbstractConstant<T> other = o;
        int returnCode;

        returnCode = hashCode() - other.hashCode();
        if (returnCode != 0) {
            return returnCode;
        }

        if (uniquifier < other.uniquifier) {
            return -1;
        }
        if (uniquifier > other.uniquifier) {
            return 1;
        }

        throw new Error("failed to compare two different constants");
    }

}

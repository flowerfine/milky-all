package cn.sliew.milky.common.constant;

/**
 * 单例，支持{@code ==}操作符的安全比较。由{@link ConstantPool}创建和管理。
 */
public interface Constant<T extends Constant<T>> extends Comparable<T> {

    /**
     * 返回分配给当前{@link Constant}的唯一数字。
     */
    int id();

    /**
     * 返回当前{@link Constant}的名字(name)。
     */
    String name();
}

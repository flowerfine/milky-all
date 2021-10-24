package cn.sliew.milky.common.function;

@FunctionalInterface
public interface CheckedSupplier<T, E extends Throwable> {
    T get() throws E;
}

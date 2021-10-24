package cn.sliew.milky.common.function;

@FunctionalInterface
public interface CheckedFunction<T, R, E extends Throwable> {
    R apply(T t) throws E;
}

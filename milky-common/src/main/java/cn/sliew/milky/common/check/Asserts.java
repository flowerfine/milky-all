package cn.sliew.milky.common.check;

/**
 * <a href="https://github.com/google/guava/wiki/ConditionalFailuresExplained">Conditional failures explained</a>
 * guava cache的一篇关于条件检查失败的描述。在大多数时候 {@code if (!condition) throw new RuntimeException();} 即可。
 * 但是深入思考下去，关于条件检查也有不同的区别。
 * <p>
 * 1. 检查调用者是否遵照方法声明正确地使用，如注释的要求或者@NonNull，@Nullable等规范
 * 2. 类内断言。如果类本身出现异常，如错误的状态等。常见的有后置检查，类不变量，私有方法的内部检查
 * 3. 确认检查。当不确定使用的api是否满足条件时做的处理，如果满足条件则调用，不满足则不调用。
 * 4. 测试断言。测试代码中使用，确保测试代码正确执行。
 * 5. 异常结果。当调用方法正常返回时，但是返回的结果是错误的，比如两个int类型相加，返回结果因为溢出而错误，
 *    但是程序没有任何异常抛出。
 */
public enum Asserts {
    ;

    public static void asserts(boolean assertion) {
        if (!assertion)
            throw new AssertionError("assertion failed");
    }

    public static void asserts(boolean assertion, Object message) {
        if (!assertion)
            throw new AssertionError("assertion failed: " + message);
    }
}

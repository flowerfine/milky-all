package cn.sliew.milky.common.exception;

public class ExceptionFactory {

    /**
     * todo Explanation
     */
    public static Exception wrapException(String message, Exception e) {
        return new BizException(message, e);
    }

    private ExceptionFactory() {
        throw new IllegalStateException("no instance");
    }
}

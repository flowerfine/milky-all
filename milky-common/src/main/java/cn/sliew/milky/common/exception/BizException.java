package cn.sliew.milky.common.exception;

public class BizException extends RuntimeException {

    private static final long serialVersionUID = 9196589398441900408L;

    protected long code = BizExceptionEnum.FAILURE.getCode();

    protected boolean retryable = BizExceptionEnum.FAILURE.isRetryable();

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(long code, boolean retryable, String message) {
        super(message);
        this.code = code;
        this.retryable = retryable;
    }

    public BizException(BizExceptionEnum codeEnum) {
        super(codeEnum.getMessage());
        this.code = codeEnum.getCode();
        this.retryable = codeEnum.isRetryable();
    }

    public Long getCode() {
        return this.code;
    }

    public boolean isRetryable() {
        return retryable;
    }
}

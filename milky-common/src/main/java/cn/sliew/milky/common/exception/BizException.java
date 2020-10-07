package cn.sliew.milky.common.exception;

public class BizException extends RuntimeException {

    protected Long code;

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

    public BizException(Long code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(BizExceptionEnum codeEnum) {
        super(codeEnum.getMessage());
        this.code = codeEnum.getCode();
    }

    public Long getCode() {
        return this.code;
    }
}

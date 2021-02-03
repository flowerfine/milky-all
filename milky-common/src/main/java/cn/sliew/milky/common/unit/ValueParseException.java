package cn.sliew.milky.common.unit;

import cn.sliew.milky.common.exception.BizException;
import cn.sliew.milky.common.exception.BizExceptionEnum;

public class ValueParseException extends BizException {

    private static final long serialVersionUID = -2137471080439797604L;

    public ValueParseException() {
        super();
    }

    public ValueParseException(String message) {
        super(message);
    }

    public ValueParseException(Throwable cause) {
        super(cause);
    }

    public ValueParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueParseException(long code, boolean retryable, String message) {
        super(code, retryable, message);
    }

    public ValueParseException(BizExceptionEnum codeEnum) {
        super(codeEnum);
    }
}

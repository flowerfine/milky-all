package cn.sliew.milky.common.parse;

import cn.sliew.milky.common.exception.BizException;
import cn.sliew.milky.common.exception.BizExceptionEnum;

public class TokenParseException extends BizException {

    private static final long serialVersionUID = -1788483700757895974L;

    public TokenParseException() {
    }

    public TokenParseException(String message) {
        super(message);
    }

    public TokenParseException(Throwable cause) {
        super(cause);
    }

    public TokenParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenParseException(long code, boolean retryable, String message) {
        super(code, retryable, message);
    }

    public TokenParseException(BizExceptionEnum codeEnum) {
        super(codeEnum);
    }
}

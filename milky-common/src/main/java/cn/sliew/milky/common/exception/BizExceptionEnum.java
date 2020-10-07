package cn.sliew.milky.common.exception;

public enum BizExceptionEnum {

    SUCCESS(0L, true, "success"),
    FAILURE(1L, false, "failure"),

    SYS_EXCEPTION(100L, false, "server exception"),
    UNKNOWN_EXCEPTION(101L, false, "unknown exception"),
    UNDEFINED_EXCEPTION(102L, false, "undefined exception"),
    LIMITED_EXCEPTION(103L, true, "limited exception"),
    FUSED_EXCEPTION(104L, true, "fused exception"),
    DEMOTED_EXCEPTION(105L, true, "demoted exception"),

    BLANK_PARAM_EXCEPTION(200L, false, "blank param"),
    INVALID_PARAM_EXCEPTION(201L, false, "invalid param"),

    EXIST_DATA_EXCEPTION(300L, false, "data already exists"),
    INVALID_DATA_EXCEPTION(301L, false, "data invalid"),
    NOT_FOUND_DATA_EXCEPTION(302L, false, "data not found"),
    INVALID_FORMAT_DATA_EXCEPTION(304L, false, "data invalid format"),

    RPC_INVOKE_EXCEPTION(400L, false, "rpc invoke error"),
    HTTP_INVOKE_EXCEPTION(401L, false, "http invoke error"),

    /**
     * 下面各系统异常枚举
     */
    AAAA(1001000001L, false, "1001000001L~1001999999L"),
    BBBB(1002000001L, false, "1002000001L~1002999999L"),
    CCCC(1003000001L, false, "1003000001L~1003999999L"),
    ;
    private long code;
    private boolean retryable;
    private String message;

    BizExceptionEnum(long code, boolean retryable, String message) {
        this.code = code;
        this.retryable = retryable;
        this.message = message;
    }


    public long getCode() {
        return code;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public String getMessage() {
        return message;
    }
}

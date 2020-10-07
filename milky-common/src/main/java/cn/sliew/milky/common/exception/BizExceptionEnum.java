package cn.sliew.milky.common.exception;

public enum BizExceptionEnum {

    SUCCESS(0L, "success"),
    FAILURE(1L, "failure"),

    SYS_EXCEPTION(100L, "server exception"),
    UNKNOWN_EXCEPTION(101L, "unknown exception"),
    UNDEFINED_EXCEPTION(102L, "undefined exception"),

    BLANK_PARAM_EXCEPTION(200L, "blank param"),
    INVALID_PARAM_EXCEPTION(201L, "invalid param"),

    EXIST_DATA_EXCEPTION(300L, "data already exists"),
    INVALID_DATA_EXCEPTION(301L, "data invalid"),
    NOT_FOUND_DATA_EXCEPTION(302L, "data not found"),
    INVALID_FORMAT_DATA_EXCEPTION(304L, "data invalid format"),

    RPC_INVOKE_EXCEPTION(400L, "rpc invoke error"),
    HTTP_INVOKE_EXCEPTION(401L, "http invoke error"),

    /**
     * 下面各系统异常枚举
     */
    AAAA(1001000001L, "1001000001L~1001999999L"),
    BBBB(1002000001L, "1002000001L~1002999999L"),
    CCCC(1003000001L, "1003000001L~1003999999L"),
    ;
    private long code;
    private String message;

    BizExceptionEnum(long code, String message) {
        this.code = code;
        this.message = message;
    }


    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

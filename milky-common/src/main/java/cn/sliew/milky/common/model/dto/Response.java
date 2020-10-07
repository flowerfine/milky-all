package cn.sliew.milky.common.model.dto;

import cn.sliew.milky.common.exception.BizException;
import cn.sliew.milky.common.exception.BizExceptionEnum;

import java.io.Serializable;

public class Response<T> implements Serializable {

    private static final long serialVersionUID = 5359808875847456900L;

    private long code;

    private String message;

    private boolean success = false;

    private boolean retryable = false;

    private T data;

    public long code() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String message() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean success() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean retryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public T data() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static Response failure() {
        return failure(BizExceptionEnum.FAILURE);
    }

    public static Response failure(long code) {
        Response response = failure(BizExceptionEnum.SYS_EXCEPTION);
        response.setCode(code);
        return response;
    }

    public static Response failure(String message) {
        Response response = failure(BizExceptionEnum.SYS_EXCEPTION);
        response.setMessage(message);
        return response;
    }

    public static Response failure(long code, String message) {
        return failure(code, false, message);
    }


    public static Response failure(long code, boolean retryable, String message) {
        Response response = new Response();
        response.setSuccess(false);
        response.setCode(code);
        response.setMessage(message);
        response.setRetryable(retryable);
        return response;
    }

    public static Response failure(Exception ex) {
        return failure(ex.getMessage());
    }

    public static Response failure(BizException ex) {
        return failure(ex.getCode(), ex.isRetryable(), ex.getMessage());
    }

    public static Response failure(BizExceptionEnum ex) {
        Response response = new Response();
        response.setSuccess(false);
        response.setCode(ex.getCode());
        response.setMessage(ex.getMessage());
        response.setRetryable(ex.isRetryable());
        return response;
    }

    public static <T> Response success(T data) {
        Response<T> response = new Response();
        response.setData(data);
        response.setSuccess(true);
        response.setCode(BizExceptionEnum.SUCCESS.getCode());
        response.setMessage(BizExceptionEnum.SUCCESS.getMessage());
        response.setRetryable(BizExceptionEnum.SUCCESS.isRetryable());
        return response;
    }
}

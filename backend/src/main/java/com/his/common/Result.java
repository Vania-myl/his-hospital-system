package com.his.common;

import lombok.Data;

/**
 * 统一返回体：{ code, msg, data }
 */
@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return build(ResultCode.SUCCESS, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return build(ResultCode.SUCCESS, "success", data);
    }

    public static <T> Result<T> error(String msg) {
        return build(ResultCode.INTERNAL_ERROR, msg, null);
    }

    public static <T> Result<T> error(int code, String msg) {
        return build(code, msg, null);
    }

    public static <T> Result<T> build(int code, String msg, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}

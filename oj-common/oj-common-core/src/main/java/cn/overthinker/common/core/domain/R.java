package cn.overthinker.common.core.domain;


import cn.overthinker.common.core.enums.ResultCode;
import lombok.Data;

@Data
public class R<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok() {
        return assembleResult(null, ResultCode.SUCCESS);
    }

    public static <T> R<T> ok(T data) {
        return assembleResult(data, ResultCode.SUCCESS);
    }

    public static <T> R<T> fail() {
        return assembleResult(null, ResultCode.FAILED);
    }

    public static <T> R<T> fail(ResultCode resultCode) {
        return assembleResult(null, resultCode);
    }

    public static <T> R<T> fail(int code, String msg) {
        return assembleResult(code, msg, null);
    }

    /**
     * 指定错误码
     *
     * @param resultCode 指定错误码
     * @param <T>
     * @return
     */
    public static <T> R<T> assembleResult(T data, ResultCode resultCode) {
        R<T> r = new R<>();
        r.setCode(resultCode.getCode());
        r.setMsg(resultCode.getMsg());
        r.setData(data);
        return r;
    }

    public static <T> R<T> assembleResult(int code, String msg, T data) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

}


package com.autolearn.icve.entity.icve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 响应结构
 * @author 胡江斌
 * @version 1.0
 * @title: LoginCacheVO
 * @projectName autolearn
 * @description: TODO
 * @date 2020/5/6 20:45
 */
@Data
@ToString
@AllArgsConstructor
public class ResultVO<T> implements Serializable {

    private Integer code;

    private String msg;

    private T data;

    private ResultVO() {
    }

    public static <T> ResultVO<T> ok() {
        return ok("ok");
    }

    public static <T> ResultVO<T> ok(String msg) {
        return ok(msg, null);
    }

    public static <T> ResultVO<T> ok(String msg, T data) {
        return build(200, msg, data);
    }

    public static <T> ResultVO<T> fail() {
        return fail("fail");
    }

    public static <T> ResultVO<T> fail(String msg) {
        return fail(msg, null);
    }

    public static <T> ResultVO<T> fail(String msg, T data) {
        return build(500, msg, data);
    }

    public static <T> ResultVO<T> build(Integer code, String msg, T data) {
        return new ResultVO<>(code, msg, data);
    }

}

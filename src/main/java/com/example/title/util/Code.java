package com.example.title.util;

/**
 * @Create: 26/04/2020 13:24
 * @Author: Q
 */
public enum Code {

//    用户造成的错误 成功 系统造成的错误
    USER_ERROR(400),SUCCESS(200),SYSTEM_ERROR(500);

    private Integer code;

    Code(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}

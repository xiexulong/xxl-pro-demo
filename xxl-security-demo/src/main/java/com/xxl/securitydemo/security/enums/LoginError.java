package com.xxl.securitydemo.security.enums;

import java.util.HashMap;
import java.util.Map;

public enum LoginError {
    FAILURE(0, "登录失败！"),

    BADCREDENTIALS(1, "用户名密码错误！"),

    LOCKED(2, "用户已被锁定，无法登录！"),

    ACCOUNTEXPIRED(3, "用户已过时，无法登录！"),

    USERNAMENOTFOUND(4, "用户不存在！");

    private Integer type;

    private String message;

    private final static Map<Integer, LoginError> mappings = new HashMap<>();

    static {
        mappings.put(FAILURE.type, FAILURE);
        mappings.put(BADCREDENTIALS.type, BADCREDENTIALS);
        mappings.put(LOCKED.type, BADCREDENTIALS);
        mappings.put(ACCOUNTEXPIRED.type, BADCREDENTIALS);
        mappings.put(USERNAMENOTFOUND.type, BADCREDENTIALS);

    }

    public static LoginError resolve(Integer type) {
        return type != null ? mappings.get(type) : null;
    }

    public static String getMessage(Integer type) {
        LoginError loginError = resolve(type);

        return loginError != null ? loginError.message : null;
    }

    LoginError(Integer type, String message) {
        this.type = type;
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

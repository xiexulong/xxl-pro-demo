package com.xxl.securitydemo.security;

import org.springframework.security.core.AuthenticationException;

public class EmptyCaptchaAuthenticationException extends AuthenticationException {

    public EmptyCaptchaAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public EmptyCaptchaAuthenticationException(String msg) {
        super(msg);
    }
}

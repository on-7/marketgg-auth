package com.nhnacademy.marketgg.auth.exception;

import com.nhnacademy.marketgg.auth.aop.advice.ExceptionMessageCode;

public class UnAuthorizationException extends AuthException {

    private static final String MESSAGE = "인증되지 않은 사용자입니다.";

    public UnAuthorizationException() {
        super(MESSAGE, ExceptionMessageCode.UNAUTHORIZATION);
    }

}

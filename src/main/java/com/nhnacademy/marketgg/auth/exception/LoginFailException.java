package com.nhnacademy.marketgg.auth.exception;

import com.nhnacademy.marketgg.auth.controller.advice.ExceptionMessageCode;

public class LoginFailException extends AuthException {

    private static final ExceptionMessageCode exceptionCode = ExceptionMessageCode.LOGIN_FAIL;

    public LoginFailException() {
        super("로그인에 실패했습니다.", ExceptionMessageCode.LOGIN_FAIL);
    }

    public String getExceptionCode() {
        return exceptionCode.msg;
    }

}

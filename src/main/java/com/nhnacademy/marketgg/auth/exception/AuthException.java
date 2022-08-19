package com.nhnacademy.marketgg.auth.exception;

import com.nhnacademy.marketgg.auth.aop.advice.ExceptionMessageCode;

public class AuthException extends RuntimeException {

    private final ExceptionMessageCode exceptionCode;

    public AuthException(String message, ExceptionMessageCode exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public String getExceptionCode() {
        return this.exceptionCode.msg;
    }

}

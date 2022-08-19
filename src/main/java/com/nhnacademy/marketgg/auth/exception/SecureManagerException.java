package com.nhnacademy.marketgg.auth.exception;

import com.nhnacademy.marketgg.auth.aop.advice.ExceptionMessageCode;

public class SecureManagerException extends RuntimeException {

    private static final ExceptionMessageCode exceptionCode = ExceptionMessageCode.SECURE_MANAGER;

    public SecureManagerException() {
        super("Secure Manager Error!!");
    }

    public String getExceptionCode() {
        return exceptionCode.msg;
    }

}

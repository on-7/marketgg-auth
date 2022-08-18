package com.nhnacademy.marketgg.auth.exception;

import com.nhnacademy.marketgg.auth.aop.advice.ExceptionMessageCode;

public class InvalidLoginRequestException extends AuthException {

    public InvalidLoginRequestException(String msg) {
        super(msg, ExceptionMessageCode.INVALID_LOGIN_REQUEST);
    }

}

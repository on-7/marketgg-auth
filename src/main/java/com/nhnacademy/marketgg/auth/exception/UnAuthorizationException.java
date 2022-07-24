package com.nhnacademy.marketgg.auth.exception;

public class UnAuthorizationException extends IllegalAccessException {

    private static final String MESSAGE = "인증되지 않은 사용자입니다.";

    public UnAuthorizationException() {
        super(MESSAGE);
    }

}

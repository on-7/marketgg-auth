package com.nhnacademy.marketgg.auth.exception;

public class AuthWithDrawOverlapException extends IllegalAccessException {
    private static final String MESSAGE = " 해당 회원은 이미 탈퇴한 상태입니다.";

    public AuthWithDrawOverlapException() {
        super(MESSAGE);
    }

}

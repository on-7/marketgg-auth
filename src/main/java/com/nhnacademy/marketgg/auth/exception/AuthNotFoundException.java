package com.nhnacademy.marketgg.auth.exception;

public class AuthNotFoundException extends IllegalArgumentException {

    private static final String MESSAGE = "회원을 찾을 수 없습니다.";

    public AuthNotFoundException(String email) {
        super(email + " " + MESSAGE);
    }

    public AuthNotFoundException() {
        super(MESSAGE);
    }

}

package com.nhnacademy.marketgg.auth.exception;

public class AuthNotFoundException extends IllegalArgumentException {

    private static final String ERROR = "회원을 찾을 수 없습니다.";
    public AuthNotFoundException(String email) {
        super(email + ERROR);
    }

}

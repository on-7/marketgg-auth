package com.nhnacademy.marketgg.auth.exception;

public class AuthNotFoundException extends IllegalArgumentException {

    public AuthNotFoundException(String email) {
        super(email + " 회원을 찾을 수 없습니다.");
    }

}

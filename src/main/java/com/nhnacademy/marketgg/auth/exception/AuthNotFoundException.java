package com.nhnacademy.marketgg.auth.exception;

public class AuthNotFoundException extends IllegalArgumentException {

    public AuthNotFoundException(String username) {
        super(username + " 회원을 찾을 수 없습니다.");
    }
}

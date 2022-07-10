package com.nhnacademy.marketgg.auth.exception;

public class LoginFailException extends IllegalArgumentException {

    public LoginFailException() {
        super("로그인에 실패했습니다.");
    }

}

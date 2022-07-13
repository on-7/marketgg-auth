package com.nhnacademy.marketgg.auth.exception;

public class OAuthRequestFailException extends IllegalArgumentException {

    public OAuthRequestFailException() {
        super("소셜 로그인 실패");
    }

}

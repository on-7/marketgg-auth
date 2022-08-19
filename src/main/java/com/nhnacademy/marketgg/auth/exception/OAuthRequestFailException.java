package com.nhnacademy.marketgg.auth.exception;

import com.nhnacademy.marketgg.auth.aop.advice.ExceptionMessageCode;

public class OAuthRequestFailException extends AuthException {

    public OAuthRequestFailException() {
        super("소셜 로그인 실패", ExceptionMessageCode.OAUTH_REQUEST_FAIL);
    }

}

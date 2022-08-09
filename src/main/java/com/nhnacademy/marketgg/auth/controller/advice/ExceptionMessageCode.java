package com.nhnacademy.marketgg.auth.controller.advice;

/**
 * 예외 메세지 국제화를 위한 메세지 코드 모음입니다.
 *
 * @author 윤동열
 */
public enum ExceptionMessageCode {

    USER_NOT_FOUND("userNotFound.msg"),
    AUTH_WITHDRAW_OVERLAP("authWithdrawOverlap.msg"),
    EMAIL_OVERLAP("emailOverlap.msg"),
    INVALID_LOGIN_REQUEST("invalidLoginRequest.msg"),
    LOGIN_FAIL("loginFail.msg"),
    SECURE_MANAGER("secureManager.msg"),
    OAUTH_REQUEST_FAIL("oauthRequestFail.msg"),
    UNAUTHORIZATION("unAuthorization.msg"),
    INVALID_REQUEST("invalidRequest.msg");

    ExceptionMessageCode(String msg) {
        this.msg = msg;
    }

    public final String msg;

}

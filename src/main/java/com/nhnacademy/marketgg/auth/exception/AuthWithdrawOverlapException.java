package com.nhnacademy.marketgg.auth.exception;

import com.nhnacademy.marketgg.auth.aop.advice.ExceptionMessageCode;

public class AuthWithdrawOverlapException extends ConflictException {

    private static final String MESSAGE = " 해당 회원은 이미 탈퇴한 상태입니다.";

    public AuthWithdrawOverlapException() {
        super(MESSAGE, ExceptionMessageCode.AUTH_WITHDRAW_OVERLAP);
    }

}

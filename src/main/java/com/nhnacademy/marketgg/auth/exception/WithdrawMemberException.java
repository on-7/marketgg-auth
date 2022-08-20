package com.nhnacademy.marketgg.auth.exception;

import static com.nhnacademy.marketgg.auth.aop.advice.ExceptionMessageCode.WITHDRAW_MEMBER;

public class WithdrawMemberException extends AuthException {

    private static final String MESSAGE = "탈퇴한 회원입니다.";

    public WithdrawMemberException() {
        super(MESSAGE, WITHDRAW_MEMBER);
    }

}

package com.nhnacademy.marketgg.auth.exception;

import com.nhnacademy.marketgg.auth.controller.advice.ExceptionMessageCode;

public class EmailOverlapException extends ConflictException {

    private static final String ERROR = "해당 이메일은 중복 되었습니다.";

    public EmailOverlapException(String email) {
        super(email + ERROR, ExceptionMessageCode.EMAIL_OVERLAP);
    }

}

package com.nhnacademy.marketgg.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailOverlapException extends IllegalArgumentException {

    private static final String ERROR = "해당 이메일은 중복 되었습니다.";
    
    public EmailOverlapException(String email) {
        super(email + ERROR);
    }

}

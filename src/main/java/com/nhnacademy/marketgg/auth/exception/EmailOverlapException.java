package com.nhnacademy.marketgg.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmailOverlapException extends IllegalArgumentException {
    public EmailOverlapException(String email, String message) {
        super(email + message);
    }

}

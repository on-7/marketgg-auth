package com.nhnacademy.marketgg.auth.exception;

public class EmailOverlapException extends IllegalArgumentException {
    public EmailOverlapException(String email, String message) {
        super(email + message);
    }

}

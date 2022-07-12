package com.nhnacademy.marketgg.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class IllegalTokenException extends AuthenticationException {

    public IllegalTokenException() {
        super("존재하는 토큰이 없습니다.");
    }

}

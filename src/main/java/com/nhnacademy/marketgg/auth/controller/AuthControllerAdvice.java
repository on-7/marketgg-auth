package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

/**
 * 전역 예외 처리
 *
 * @version 1.0.0
 */
@RestControllerAdvice
public class AuthControllerAdvice {

    /**
     * 잘못된 HTTP Method 로 요청 시 발생하는 예외를 처리합니다.
     *
     * @return 405 Http Status 를 응답합니다.
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<Void> handleMethodNotAllowException() {

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                             .build();
    }

    /**
     * 로그인 실패 시 발생하는 예외를 처리합니다.
     *
     * @return 401 Http Status 를 응답합니다.
     */
    @ExceptionHandler(value = { LoginFailException.class, AuthNotFoundException.class })
    public ResponseEntity<Void> handleLoginFailException() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .build();
    }

}

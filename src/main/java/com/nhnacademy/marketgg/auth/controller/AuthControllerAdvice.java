package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import org.springframework.data.redis.connection.RedisInvalidSubscriptionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

/**
 * 전역 예외 처리를 위한 클래스입니다.
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
     * 서버의 현재 상태와 요청이 충돌할때 예외를 처리합니다.
     * ex) 서버의 현재 상태(Redis) 와 요청(Redis 에 담긴 Key 의 중복)이 충돌했음을 나타 냄.
     *
     * @return 409 Http Conflict 를 응답합니다.
     * <a href="https://blog.outsider.ne.kr/1121" />
     * <a href="https://developer.mozilla.org/ko/docs/Web/HTTP/Status/409" />
     */
    @ExceptionHandler(value = {
        EmailOverlapException.class,
        RedisInvalidSubscriptionException.class })
    public ResponseEntity<Void> handleConflictNotAllowException() {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .build();
    }

    /**
     * 로그인 실패 시 발생하는 예외를 처리합니다.
     *
     * @return 401 Http Status 를 응답합니다.
     */
    @ExceptionHandler(value = {
        LoginFailException.class,
        AuthNotFoundException.class })
    public ResponseEntity<Void> handleLoginFailException() {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .build();
    }

}

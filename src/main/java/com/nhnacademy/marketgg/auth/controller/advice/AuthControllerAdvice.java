package com.nhnacademy.marketgg.auth.controller.advice;

import com.nhnacademy.marketgg.auth.dto.response.common.ErrorEntity;
import com.nhnacademy.marketgg.auth.exception.AuthWithDrawOverlapException;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisInvalidSubscriptionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;

/**
 * 전역 예외 처리를 위한 클래스입니다.
 *
 * @version 1.0.0
 * @author 윤동열 
 */
@Slf4j
@RestControllerAdvice
public class AuthControllerAdvice {

    /**
     * 잘못된 HTTP Method 로 요청 시 발생하는 예외를 처리합니다.
     *
     * @return 405 Http Status 를 응답합니다.
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ErrorEntity> handleMethodNotAllowException(MethodNotAllowedException e) {
        log.debug(e.toString());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(new ErrorEntity(e.getMessage()));
    }

    /**
     * 서버의 현재 상태와 요청이 충돌할때 예외를 처리합니다.
     * ex) 서버의 현재 상태(Redis) 와 요청(Redis 에 담긴 Key 의 중복)이 충돌했음을 나타 냄.
     *
     * @return 409 Http Conflict 를 응답합니다.
     * <a href="https://blog.outsider.ne.kr/1121">Http Conflict 1</a>
     * <a href="https://developer.mozilla.org/ko/docs/Web/HTTP/Status/409">Http Conflict 2</a>
     */
    @ExceptionHandler(value = {
        EmailOverlapException.class,
        RedisInvalidSubscriptionException.class,
        AuthWithDrawOverlapException.class
    })
    public ResponseEntity<ErrorEntity> handleConflictNotAllowException(Exception e) {
        log.debug(e.toString());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(new ErrorEntity(e.getMessage()));
    }

    /**
     * 로그인 실패 시 발생하는 예외를 처리합니다.
     *
     * @return 401 Http Status 를 응답합니다.
     */
    @ExceptionHandler(value = {
        LoginFailException.class
    })
    public ResponseEntity<ErrorEntity> handleLoginFailException(LoginFailException e) {
        log.debug(e.toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(new ErrorEntity(e.getMessage()));
    }

}

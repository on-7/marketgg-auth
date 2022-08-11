package com.nhnacademy.marketgg.auth.exception;

import com.nhnacademy.marketgg.auth.controller.advice.ExceptionMessageCode;

/**
 * 409 예외처리를 위한 추상클래스입니다.
 *
 * @author 김훈민, 윤동열
 */
public abstract class ConflictException extends RuntimeException {

    private final ExceptionMessageCode exceptionCode;

    public ConflictException(String message, ExceptionMessageCode exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public String getExceptionCode() {
        return this.exceptionCode.msg;
    }

}

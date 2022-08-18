package com.nhnacademy.marketgg.auth.aop.advice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.nhnacademy.marketgg.auth.dto.response.common.AuthResult;
import com.nhnacademy.marketgg.auth.dto.response.common.ErrorEntity;
import com.nhnacademy.marketgg.auth.exception.SecureManagerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리를 위한 클래스입니다.
 *
 * @author 윤동열
 * @version 1.0.0
 */
@Slf4j
@Order
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {

    private final MessageSource messageSource;

    /**
     * Secure Manager 에서 오류 발생 시 처리합니다.
     *
     * @param e - Secure Manger Exception
     * @return 500 Code
     * @author 윤동열
     */
    @ExceptionHandler(SecureManagerException.class)
    public ResponseEntity<AuthResult<Void>> handleSecureManager(SecureManagerException e) {
        log.error(e.getMessage());

        String msg = messageSource.getMessage(e.getExceptionCode(), null, LocaleContextHolder.getLocale());
        ErrorEntity error = new ErrorEntity(msg);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                             .contentType(APPLICATION_JSON)
                             .body(AuthResult.error(error));
    }

    /**
     * 기타 예외 발생 시 처리합니다.
     *
     * @param e - Secure Manger Exception
     * @return 500 Code
     * @author 윤동열
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthResult<Void>> handleException(Exception e) {
        log.error("", e);
        ErrorEntity error = new ErrorEntity(e.getMessage());

        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                             .contentType(APPLICATION_JSON)
                             .body(AuthResult.error(error));
    }

}

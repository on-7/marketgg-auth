package com.nhnacademy.marketgg.auth.controller.advice;

import static com.nhnacademy.marketgg.auth.controller.advice.ExceptionMessageCode.*;

import com.nhnacademy.marketgg.auth.dto.response.common.CommonResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.ErrorEntity;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 사용자 정보 요청 시 발생하는 예외를 처리합니다.
 *
 * @author 윤동열
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class AuthInfoControllerAdvice {

    private final MessageSource messageSource;

    /**
     * 사용자 정보 요청 시 발생하는 예외를 처리합니다.
     *
     * @param e - 발생한 예외
     * @return - 클라이언트 오류 코드를 반환합니다.
     */
    @ExceptionHandler({
        AuthNotFoundException.class
    })
    public ResponseEntity<CommonResponse> errorControl(Exception e) {
        log.debug(e.toString());

        String msg = messageSource.getMessage(USER_NOT_FOUND.msg, null, LocaleContextHolder.getLocale());
        ErrorEntity error = new ErrorEntity(msg);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(error);
    }

}

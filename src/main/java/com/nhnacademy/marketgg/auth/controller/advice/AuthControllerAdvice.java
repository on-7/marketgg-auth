package com.nhnacademy.marketgg.auth.controller.advice;

import static com.nhnacademy.marketgg.auth.controller.advice.ExceptionMessageCode.INVALID_REQUEST;

import com.nhnacademy.marketgg.auth.dto.response.common.ErrorEntity;
import com.nhnacademy.marketgg.auth.exception.AuthException;
import com.nhnacademy.marketgg.auth.exception.AuthWithdrawOverlapException;
import com.nhnacademy.marketgg.auth.exception.ConflictException;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.exception.InvalidLoginRequestException;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import com.nhnacademy.marketgg.auth.exception.OAuthRequestFailException;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
 * @author 윤동열
 * @version 1.0.0
 * @
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class AuthControllerAdvice {

    private final MessageSource messageSource;

    /**
     * 잘못된 HTTP Method 로 요청 시 발생하는 예외를 처리합니다.
     *
     * @return 405 Http Status 를 응답합니다.
     * @author 윤동열
     */
    @ExceptionHandler(MethodNotAllowedException.class)
    public ResponseEntity<ErrorEntity> handleMethodNotAllowException(MethodNotAllowedException e) {
        log.debug("", e);

        String msg = messageSource.getMessage(INVALID_REQUEST.msg, null, LocaleContextHolder.getLocale());
        ErrorEntity error = new ErrorEntity(msg);

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(error);
    }

    /**
     * 서버의 현재 상태와 요청이 충돌할때 예외를 처리합니다.
     * ex) 서버의 현재 상태(Redis) 와 요청(Redis 에 담긴 Key 의 중복)이 충돌했음을 나타 냄.
     *
     * @return 409 Http Conflict 를 응답합니다.
     * @author 김훈민
     * @see <a href="https://blog.outsider.ne.kr/1121">Http Conflict 1</a>
     * @see <a href="https://developer.mozilla.org/ko/docs/Web/HTTP/Status/409">Http Conflict 2</a>
     */
    @ExceptionHandler(value = {
        EmailOverlapException.class,
        AuthWithdrawOverlapException.class
    })
    public ResponseEntity<ErrorEntity> handleConflictNotAllowException(ConflictException e) {
        log.debug(e.toString());

        String msg = messageSource.getMessage(e.getExceptionCode(), null, LocaleContextHolder.getLocale());
        ErrorEntity error = new ErrorEntity(msg);

        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(error);
    }

    /**
     * 서버의 현재 상태와 요청이 충돌할때 예외를 처리합니다.
     * ex) 서버의 현재 상태(Redis) 와 요청(Redis 에 담긴 Key 의 중복)이 충돌했음을 나타 냄.
     *
     * @return 409 Http Conflict 를 응답합니다.
     * @author 김훈민
     * @see <a href="https://blog.outsider.ne.kr/1121">Http Conflict 1</a>
     * @see <a href="https://developer.mozilla.org/ko/docs/Web/HTTP/Status/409">Http Conflict 2</a>
     */
    @ExceptionHandler(value = {
        RedisInvalidSubscriptionException.class
    })
    public ResponseEntity<ErrorEntity> handleRedisConflictNotAllowException(RedisInvalidSubscriptionException e) {
        log.debug(e.toString());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(new ErrorEntity(e.getMessage()));
    }

    /**
     * 인증 실패 시 발생하는 예외를 처리합니다.
     *
     * @return 401 Http Status 를 응답합니다.
     * @author 윤동열
     */
    @ExceptionHandler(value = {
        LoginFailException.class,
        InvalidLoginRequestException.class,
        UnAuthorizationException.class,
        OAuthRequestFailException.class
    })
    public ResponseEntity<ErrorEntity> handleLoginFailException(AuthException e) {
        log.debug(e.toString());

        String msg = messageSource.getMessage(e.getExceptionCode(), null, LocaleContextHolder.getLocale());
        ErrorEntity error = new ErrorEntity(msg);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(error);
    }

}

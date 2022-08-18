package com.nhnacademy.marketgg.auth.dto.response.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Market GG 에서 공통 응답 객체입니다.
 *
 * @param <T> - 응답 객체 타입
 * @author 김훈민
 * @author 윤동열
 * @version 1.0
 * @since 1.0
 */
@RequiredArgsConstructor
@Getter
public class AuthResult<T> {

    private final boolean success;

    private final T data;

    private final ErrorEntity error;

    public static <T> AuthResult<T> success(T data) {
        return new AuthResult<>(true, data, null);
    }

    public static <T> AuthResult<T> error(ErrorEntity error) {
        return new AuthResult<>(false, null, error);
    }

}

package com.nhnacademy.marketgg.auth.dto.response.common;

import com.nhnacademy.marketgg.auth.dto.response.login.oauth.OauthLoginResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(title = "응답 성공 여부", description = "API 요청 시 응답 결과의 성공 반환 여부를 판단하는 flag 값입니다.", example = "true")
    private final boolean success;

    @Schema(title = "응답 결과 데이터", description = "API 요청에 대한 응답 결과 데이터입니다.",
        anyOf = { OauthLoginResponse.class })
    private final T data;

    private final ErrorEntity error;

    public static <T> AuthResult<T> success(T data) {
        return new AuthResult<>(true, data, null);
    }

    public static <T> AuthResult<T> error(ErrorEntity error) {
        return new AuthResult<>(false, null, error);
    }

}

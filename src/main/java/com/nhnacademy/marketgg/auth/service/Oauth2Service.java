package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.response.OauthLoginResponse;

/**
 * 소셜 로그인 비즈니스 로직을 처리합니다.
 *
 * @author 윤동열
 */
public interface Oauth2Service {

    /**
     * 사용자의 프로필 정보를 요청합니다.
     *
     * @param code - 벤더에서 응답한 로그인 요청 시 필요한 코드 문자열
     * @return 유효한 사용자의 프로필 정보
     */
    OauthLoginResponse requestProfile(String code);

}

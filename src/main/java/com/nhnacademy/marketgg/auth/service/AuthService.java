package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.EmailUseRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.request.UpdateRequest;
import com.nhnacademy.marketgg.auth.dto.response.SignUpResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.dto.response.ExistEmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.UseEmailResponse;

import javax.management.relation.RoleNotFoundException;

/**
 * 인증 관련 비즈니스 로직을 처리하는 클래스입니다.
 *
 * @version 1.0.0
 */
public interface AuthService {

    /**
     * 로그아웃을 진행합니다.
     *
     * @param token - 로그아웃하려는 사용자의 JWT 입니다.
     * @since 1.0.0
     */
    void logout(final String token);

    /**
     * JWT 를 갱신합니다.
     *
     * @param token - 만료된 JWT 입니다.
     * @return 새로운 JWT 를 반환합니다.
     * @since 1.0.0
     */
    TokenResponse renewToken(final String token);

}

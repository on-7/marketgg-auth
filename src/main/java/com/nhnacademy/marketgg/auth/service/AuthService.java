package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import javax.management.relation.RoleNotFoundException;

/**
 * 인증 관련 비즈니스 로직을 처리하는 클래스입니다.
 *
 * @version 1.0.0
 */
public interface AuthService {

    void signup(final SignUpRequest signUpRequest) throws RoleNotFoundException;
    
    /**
     * 로그아웃을 진행합니다.
     *
     * @param token - 로그아웃하려는 사용자의 JWT 입니다.
     * @since 1.0.0
     */
    void logout(final String token);

    EmailResponse checkEmail(final String email) throws EmailOverlapException;

    /**
     * JWT 를 갱신합니다.
     *
     * @param token - 만료된 JWT 입니다.
     * @return 새로운 JWT 를 반환합니다.
     * @since 1.0.0
     */
    TokenResponse renewToken(final String token);

}

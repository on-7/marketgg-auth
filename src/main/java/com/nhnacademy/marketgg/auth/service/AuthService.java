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
     * 회원가입을 진행합니다.
     *
     * @param signUpRequest - 회원가입시 중요정보 입니다.
     * @return SignUpResponse - 회원가입시 Auth 서버에서 먼저 생성 되었던
     *                          UUID 를 Server 에 전해주기 위한 클래스입니다.
     * @throws RoleNotFoundException - 권한이 없으면 예외를 던집니다.
     */
    SignUpResponse signup(final SignUpRequest signUpRequest) throws RoleNotFoundException;
    
    /**
     * 로그아웃을 진행합니다.
     *
     * @param token - 로그아웃하려는 사용자의 JWT 입니다.
     * @since 1.0.0
     */
    void logout(final String token);

    /**
     * 이메일 중복확인을 합니다.
     *
     * @param emailRequest - 클라이언트가 입력한 이메일 객체 입니다.
     * @since 1.0.0
     */
    ExistEmailResponse checkEmail(final EmailRequest emailRequest);

    /**
     * JWT 를 갱신합니다.
     *
     * @param token - 만료된 JWT 입니다.
     * @return 새로운 JWT 를 반환합니다.
     * @since 1.0.0
     */
    TokenResponse renewToken(final String token);

    /**
     * 이메일 중복확인을 마친 회원이
     * 메일에 첨부된 버튼을 통해 사용하기를 눌렀는지 확인하는 서비스입니다.
     *
     * @param emailRequest - 메일에 담겨있는 회원의 이메일 주소입니다.
     * @since 1.0.0
     */
    UseEmailResponse useEmail(final EmailUseRequest emailRequest);

    void update(UpdateRequest updateRequest);

}

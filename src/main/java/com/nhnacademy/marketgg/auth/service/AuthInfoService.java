package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.request.AuthUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;
import java.util.List;

/**
 * 사용자 정보 요청 관련 비즈니스 로직을 처리합니다.
 *
 * @version 1.0.0
 */
public interface AuthInfoService {

    /**
     * UUID 를 이용하여 사용자를 찾아 반환합니다.
     *
     * @param token - JWT
     * @return - 사용자 정보를 반환합니다.
     * @throws UnAuthorizationException - 유효하지 않은 JWT 로 요청시 발생하는 예외입니다.
     * @author 윤동열
     */
    MemberResponse findAuthByUuid(final String token) throws UnAuthorizationException;

    /**
     * UUID 를 이용하여 사용자 정보 조회.
     *
     * @param uuid - 사용자 UUID
     * @return 사용자의 이름, 이메일
     * @author 윤동열
     */
    MemberInfoResponse findMemberInfoByUuid(final String uuid);

    /**
     * 사용자 정보를 업데이트합니다.
     *
     * @param token             -  JWT
     * @param authUpdateRequest - 사용자 업데이트 정보
     * @return - 새로운 JWT
     */
    TokenResponse update(final String token, final AuthUpdateRequest authUpdateRequest);

    /**
     * 회원탈퇴합니다.
     *
     * @param token               - JWT
     * @param authWithDrawRequest - 탈퇴한 사용자 정보
     */
    void withdraw(final String token, final AuthWithDrawRequest authWithDrawRequest);

}

package com.nhnacademy.marketgg.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.dto.Payload;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.request.MemberUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.response.AdminMemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.UuidTokenResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.PageEntity;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.springframework.data.domain.Pageable;

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
     * 사용자 목록 전체 조회.
     *
     * @return 사용자 정보
     */
    PageEntity<AdminMemberResponse> findAdminMembers(Pageable pageable);

    /**
     * UUID 목록에 해당하는 회원 목록을 조회합니다.
     *
     * @param uuids - 요청한 회원 UUID 목록
     * @return - 회원 목록
     * @author 윤동열
     */
    List<MemberNameResponse> findMemberNameList(List<String> uuids);

    /**
     * 사용자 정보를 업데이트합니다.
     *
     * @param token               -  JWT
     * @param memberUpdateRequest - 사용자 업데이트 정보
     * @return - 새로운 JWT
     * @author 김훈민
     */
    UuidTokenResponse update(final String token, final MemberUpdateRequest memberUpdateRequest);

    /**
     * 회원탈퇴합니다.
     *
     * @param token      - JWT
     * @param withdrawAt - 삭제 시간 입니다.
     * @author 김훈민
     */
    void withdraw(final String token, final AuthWithDrawRequest withdrawAt);

    /**
     * JWT 토큰으로 ADMIN 권한을 확인합니다.
     *
     * @param token - JWT
     * @return 관리자 권한 여부
     * @throws JsonProcessingException JSON 직렬화 시 발생 가능
     */
    default boolean isAdmin(String token) throws JsonProcessingException {
        String[] jwtSection = token.split("\\.");
        String jwtPayload = jwtSection[1];

        byte[] decode = Base64.getDecoder().decode(jwtPayload);
        Payload payload = new ObjectMapper().readValue(new String(decode, StandardCharsets.UTF_8), Payload.class);

        return payload.getAuthorities().contains(Roles.ROLE_ADMIN.name());
    }

}

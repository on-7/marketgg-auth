package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원가입시 marketgg-shop 서버에 응답할 클래스입니다.
 */
@RequiredArgsConstructor
@Getter
public class SignUpResponse {

    private final String uuid;
    private final String referrerUuid;

    public SignUpResponse(String uuid) {
        this.uuid = uuid;
        this.referrerUuid = null;
    }
}


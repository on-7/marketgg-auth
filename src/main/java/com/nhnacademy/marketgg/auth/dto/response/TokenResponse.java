package com.nhnacademy.marketgg.auth.dto.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 토큰을 응답합니다.
 */
@RequiredArgsConstructor
@Getter
public class TokenResponse {

    private final String jwt;
    private final LocalDateTime expiredDate;

}

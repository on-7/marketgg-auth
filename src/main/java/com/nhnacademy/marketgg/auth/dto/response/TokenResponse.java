package com.nhnacademy.marketgg.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 토큰을 응답합니다.
 */
@AllArgsConstructor
@Getter
public class TokenResponse {

    private final String jwt;
}

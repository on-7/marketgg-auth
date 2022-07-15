package com.nhnacademy.marketgg.auth.jwt;

import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Refresh Token 을 Redis 에 저장할 때 사용하는 클래스입니다.
 */

@RequiredArgsConstructor
@Getter
public class RefreshToken implements Serializable {

    private final String email;
    private final String token;

}

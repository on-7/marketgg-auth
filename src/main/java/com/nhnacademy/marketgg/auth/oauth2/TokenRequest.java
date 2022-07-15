package com.nhnacademy.marketgg.auth.oauth2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TokenRequest {

    private final String clientId;
    private final String clientSecret;
    private final String code;

}

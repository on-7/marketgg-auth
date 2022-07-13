package com.nhnacademy.marketgg.auth.oauth2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenRequest {

    private final String clientId;
    private final String clientSecret;
    private final String code;
}

package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OauthResponse {

    private final boolean success;
    private final TokenResponse tokenResponse;

}

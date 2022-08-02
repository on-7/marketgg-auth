package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class OauthResponse {

    private final boolean success;
    private final TokenResponse tokenResponse;

    public abstract String getEmail();

    public abstract String getName();

    public abstract String getProvider();

}

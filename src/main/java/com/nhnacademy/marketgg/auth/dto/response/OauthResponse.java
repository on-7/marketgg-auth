package com.nhnacademy.marketgg.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class OauthResponse {

    private boolean success;
    private TokenResponse tokenResponse;

    public abstract String getEmail();

    public abstract String getName();

    public abstract String getProvider();

}

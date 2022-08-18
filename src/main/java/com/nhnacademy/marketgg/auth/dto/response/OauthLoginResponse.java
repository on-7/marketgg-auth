package com.nhnacademy.marketgg.auth.dto.response;

import java.util.Objects;
import lombok.Getter;

@Getter
public class OauthLoginResponse {

    private final TokenResponse tokenResponse;
    private final OauthProfile oauthProfile;

    private OauthLoginResponse(TokenResponse tokenResponse, OauthProfile oauthProfile) {
        this.tokenResponse = tokenResponse;
        this.oauthProfile = oauthProfile;
    }

    public boolean successLogin() {
        return Objects.nonNull(tokenResponse);
    }

    public static OauthLoginResponse loginSuccess(TokenResponse tokenResponse) {
        return new OauthLoginResponse(tokenResponse, null);
    }

    public static OauthLoginResponse doSignUp(OauthProfile oauthProfile) {
        return new OauthLoginResponse(null, oauthProfile);
    }

}

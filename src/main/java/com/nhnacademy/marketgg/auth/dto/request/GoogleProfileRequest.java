package com.nhnacademy.marketgg.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GoogleProfileRequest {

    private final String code;

    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("client_secret")
    private final String clientSecret;

    @JsonProperty("redirect_uri")
    private final String redirectUri;

    @JsonProperty("grant_type")
    private final String grantType;

    public GoogleProfileRequest(String code, String clientId, String clientSecret, String redirectUri) {
        this.code = code;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.grantType = "authorization_code";
    }

}

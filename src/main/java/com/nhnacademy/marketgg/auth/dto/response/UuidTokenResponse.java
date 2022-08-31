package com.nhnacademy.marketgg.auth.dto.response;

import com.nhnacademy.marketgg.auth.dto.response.login.oauth.TokenResponse;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class UuidTokenResponse {

    private final String jwt;
    private final LocalDateTime expiredDate;
    private String updatedUuid;

    public UuidTokenResponse(TokenResponse tokenResponse, String updatedUuid) {
        this.jwt = tokenResponse.getJwt();
        this.expiredDate = tokenResponse.getExpiredDate();
        this.updatedUuid = updatedUuid;
    }

}

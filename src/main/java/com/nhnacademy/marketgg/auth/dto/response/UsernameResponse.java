package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;

@Getter
public class UsernameResponse {

    Boolean isExistUsername;

    public UsernameResponse(Boolean existsByUsername) {
        this.isExistUsername = existsByUsername;
    }
}

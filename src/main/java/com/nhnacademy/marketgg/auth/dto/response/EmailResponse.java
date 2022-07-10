package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;

@Getter
public class EmailResponse {

    Boolean isExistEmail;

    public EmailResponse(Boolean existsByEmail) {
        this.isExistEmail = existsByEmail;
    }
}

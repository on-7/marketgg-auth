package com.nhnacademy.marketgg.auth.dto.response.common;

import lombok.Getter;

@Getter
public class ErrorEntity extends CommonResponse {

    private final String message;

    public ErrorEntity(String message) {
        super(false);
        this.message = message;
    }

}

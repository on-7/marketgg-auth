package com.nhnacademy.marketgg.auth.dto.response.common;

import lombok.Getter;

@Getter
public class ErrorEntity extends CommonResponse {

    String message;

    public ErrorEntity(boolean success, String message) {
        super(success);
        this.message = message;
    }

}

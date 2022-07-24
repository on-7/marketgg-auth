package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class AuthUpdateResponse {

    private final String deletedUuid;
    private final String uuid;
}

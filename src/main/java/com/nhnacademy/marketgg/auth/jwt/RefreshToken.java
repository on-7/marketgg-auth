package com.nhnacademy.marketgg.auth.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RefreshToken {

    private final String email;
    private final String token;

}

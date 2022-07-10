package com.nhnacademy.marketgg.auth.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Token {

    private final String refreshToken;

}

package com.nhnacademy.marketgg.auth.jwt;

import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RefreshToken implements Serializable {

    private final String email;
    private final String token;

}

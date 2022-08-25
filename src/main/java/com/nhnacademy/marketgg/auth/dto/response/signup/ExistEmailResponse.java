package com.nhnacademy.marketgg.auth.dto.response.signup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExistEmailResponse {

    private final boolean isExistEmail;

}

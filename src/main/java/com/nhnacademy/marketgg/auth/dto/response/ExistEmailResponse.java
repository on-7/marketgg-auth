package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExistEmailResponse {

    private final boolean isExistEmail;

}

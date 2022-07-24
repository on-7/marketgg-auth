package com.nhnacademy.marketgg.auth.dto.response.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class CommonResponse {

    protected final boolean success;

}

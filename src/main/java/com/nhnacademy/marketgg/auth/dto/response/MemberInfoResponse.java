package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberInfoResponse {

    private final String name;
    private final String email;
    private final String phoneNumber;

}

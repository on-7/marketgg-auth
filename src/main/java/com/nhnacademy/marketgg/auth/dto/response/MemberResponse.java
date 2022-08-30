package com.nhnacademy.marketgg.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class MemberResponse {

    @Schema(title = "사용자 이메일", description = "사용자의 이메일", example = "example@gmail.com")
    private final String email;

    @Schema(title = "사용자 이름", description = "사용자 이름", example = "홍길동")
    private final String name;

    @Schema(title = "사용자 전화번호", description = "사용자의 전화번호", example = "01012341234")
    private final String phoneNumber;

}

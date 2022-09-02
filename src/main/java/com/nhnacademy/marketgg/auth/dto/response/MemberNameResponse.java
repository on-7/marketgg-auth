package com.nhnacademy.marketgg.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MemberNameResponse {

    @Schema(title = "사용자 UUID", description = "사용자 UUID", example = "9c151cf8-47f7-41e4-b4eb-e8dcb94a6081")
    private final String uuid;

    @Schema(title = "사용자 이름", description = "사용자 이름", example = "홍길동")
    private final String name;

}

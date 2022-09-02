package com.nhnacademy.marketgg.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberInfoRequest {

    @Schema(title = "사용자 UUID", description = "정보가 필요한 회원의 UUID",
        example = "9c151cf8-47f7-41e4-b4eb-e8dcb94a6081")
    @NotNull
    @NotBlank
    private String uuid;

}

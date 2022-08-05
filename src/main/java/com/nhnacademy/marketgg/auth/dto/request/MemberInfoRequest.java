package com.nhnacademy.marketgg.auth.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MemberInfoRequest {

    @NotNull
    @NotBlank
    private String uuid;

}

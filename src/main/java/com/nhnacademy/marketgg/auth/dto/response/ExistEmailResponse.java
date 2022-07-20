package com.nhnacademy.marketgg.auth.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class ExistEmailResponse {

    private final boolean isExistEmail;

}

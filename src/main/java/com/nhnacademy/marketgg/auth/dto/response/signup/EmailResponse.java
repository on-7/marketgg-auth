package com.nhnacademy.marketgg.auth.dto.response.signup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class EmailResponse {

    private final Boolean isExistEmail;

    private final String message;

}

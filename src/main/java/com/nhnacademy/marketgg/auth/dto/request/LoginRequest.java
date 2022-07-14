package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Login 요청 정보를 담은 클래스입니다.
 */
@NoArgsConstructor
@Getter
public class LoginRequest {

    private String email;
    private String password;

}

package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 클라이언트가 폼에서 입력한 중요정보 요청 클래스 입니다.
 */
@NoArgsConstructor
@Getter
public class SignUpRequest {

    private String email;

    private String password;

    private String name;

    private String phoneNumber;

    public void encodingPassword(String password) {
        this.password = password;
    }

}

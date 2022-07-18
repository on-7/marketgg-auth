package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

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

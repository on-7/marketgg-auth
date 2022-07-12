package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class SignupRequest {

    private String email;
    private String password;

    private String name;

    public void encodingPassword(String password) {
        this.password = password;
    }

}

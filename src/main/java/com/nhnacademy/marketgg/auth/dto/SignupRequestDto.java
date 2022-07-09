package com.nhnacademy.marketgg.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class SignupRequestDto {

    private String username;

    private String password;

    private String email;

    private String name;

    public void encodingPassword(String password) {
        this.password = password;
    }

}

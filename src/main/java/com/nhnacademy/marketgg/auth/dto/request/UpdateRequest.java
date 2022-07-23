package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateRequest {

    private String email;
    private String password;
    private String name;
    private String phoneNumber;

    public void encodingPassword(final String password) {
        this.password = password;
    }

}

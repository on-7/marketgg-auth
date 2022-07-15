package com.nhnacademy.marketgg.auth.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignUpRequest {

    private String uuid;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;

    public void encodingPassword(String password) {
        this.password = password;
    }

    public String generateUUID() {
        this.uuid = UUID.randomUUID().toString();
        return this.uuid;
    }

}

package com.nhnacademy.marketgg.auth.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Getter
public class MemberUpdateRequest {

    @NotBlank
    private String password;
    @NotBlank
    private String name;
    @NotBlank
    private String phoneNumber;

    public void encodingPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

}

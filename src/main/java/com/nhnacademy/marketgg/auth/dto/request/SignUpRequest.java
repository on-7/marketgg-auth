package com.nhnacademy.marketgg.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 클라이언트가 폼에서 입력한 중요 정보 요청 클래스 입니다.
 */
@NoArgsConstructor
@Getter
@ToString
public class SignUpRequest {

    private String email;

    private String password;

    private String name;

    private String phoneNumber;

    private String referrerEmail;

    private String provider;

    public void encodingPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

}

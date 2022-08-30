package com.nhnacademy.marketgg.auth.dto.request;

import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthWithDrawRequest {

    @Email(message = "이메일 양식을 지켜주세요.")
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;

    private LocalDateTime withdrawAt;

    public void encodingPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

}

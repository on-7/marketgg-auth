package com.nhnacademy.marketgg.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Login 요청 정보를 담은 클래스입니다.
 */
@NoArgsConstructor
@Getter
public class LoginRequest {

    @Schema(title = "사용자 이메일", description = "사용자 이메일", example = "example@gmail.com")
    @Email
    @NotBlank
    private String email;

    @Schema(title = "비밀번호", description = "암호화된 비밀번호",
        example = "AE5F70C69E16A43E5D0F00444EAB424E43678AB349EFB08D56358F6A3EC181AB")
    @NotNull
    private String password;

}

package com.nhnacademy.marketgg.auth.dto.response.login.oauth.google;

import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.OauthProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.ToString;

@ToString
public class GoogleProfile implements OauthProfile {

    @Schema(title = "사용자 이메일", description = "사용자의 이메일", example = "example@gmail.com")
    private String email;

    @Schema(title = "사용자 이름", description = "사용자 이름", example = "홍길동")
    private String name;

    @Schema(title = "소셜 로그인 벤더", description = "소셜 로그인 벤더", example = "GOOGLE")
    private String provider;

    public GoogleProfile() {
        this.provider = Provider.GOOGLE.name();
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getProvider() {
        return this.provider;
    }

    public static GoogleProfile successGoogleLogin() {
        return new GoogleProfile();
    }

}

package com.nhnacademy.marketgg.auth.dto.response.login.oauth.google;

import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.OauthProfile;
import lombok.ToString;

@ToString
public class GoogleProfile implements OauthProfile {

    private String email;
    private String name;
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

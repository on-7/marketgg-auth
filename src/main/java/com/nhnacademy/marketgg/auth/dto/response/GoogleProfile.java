package com.nhnacademy.marketgg.auth.dto.response;

import com.nhnacademy.marketgg.auth.constant.Provider;
import lombok.ToString;

@ToString
public class GoogleProfile extends OauthResponse {

    private String email;
    private String name;
    private String provider;

    public GoogleProfile() {
        super(false, null);
    }

    public GoogleProfile(String email, String name) {
        super(false, null);
        this.email = email;
        this.name = name;
        this.provider = Provider.GOOGLE.name();
    }

    public GoogleProfile(boolean success, TokenResponse tokenResponse) {
        super(success, tokenResponse);
        this.email = null;
        this.name = null;
        this.provider = null;
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

}

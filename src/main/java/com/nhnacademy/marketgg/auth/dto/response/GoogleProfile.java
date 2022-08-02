package com.nhnacademy.marketgg.auth.dto.response;

import com.nhnacademy.marketgg.auth.constant.Provider;
import lombok.ToString;

@ToString
public class GoogleProfile extends OauthResponse {

    private final String email;
    private final String name;
    private final String provider;

    public GoogleProfile(String email, String name) {
        super(false, null);
        this.email = email;
        this.name = name;
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

}

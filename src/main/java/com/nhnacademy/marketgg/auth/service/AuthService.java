package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;

public interface AuthService {

    void signup(SignupRequestDto signupRequestDto);

    Boolean existsUsername(String username);

    Boolean existsEmail(String email);

    String login(LoginRequest loginRequest);

}

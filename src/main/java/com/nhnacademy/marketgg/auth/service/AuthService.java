package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.request.SignupRequest;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.UsernameResponse;

public interface AuthService {

    void signup(SignupRequest signupRequest);

    UsernameResponse existsUsername(String username);

    EmailResponse existsEmail(String email);

    String login(LoginRequest loginRequest);

}

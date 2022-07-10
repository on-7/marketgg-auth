package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;

public interface AuthService {

    String login(LoginRequest loginRequest);
}

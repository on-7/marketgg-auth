package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;

import javax.management.relation.RoleNotFoundException;

public interface AuthService {

    void signup(final SignUpRequest signUpRequest) throws RoleNotFoundException;

    String renewToken(String token);
    
    String login(LoginRequest loginRequest);

    EmailResponse checkEmail(String email) throws EmailOverlapException;

}

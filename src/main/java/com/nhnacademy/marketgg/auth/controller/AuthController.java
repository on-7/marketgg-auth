package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.RoleNotFoundException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Void> doLogin(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                             .headers(header -> header.setBearerAuth(token))
                             .build();
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> doSignup(@RequestBody final SignUpRequest signUpRequest) throws RoleNotFoundException {
        authService.signup(signUpRequest);
        return ResponseEntity.status(CREATED)
                             .build();
    }

    @PostMapping("/check/email")
    public ResponseEntity<EmailResponse> checkEmail(@RequestBody EmailRequest emailRequest) throws Exception {
        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(authService.checkEmail(emailRequest.getEmail()));
    }

}

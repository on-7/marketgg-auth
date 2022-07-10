package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import com.nhnacademy.marketgg.auth.dto.EmailRequestDto;
import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.dto.UsernameRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Void> doSignup(@RequestBody SignupRequestDto signupRequestDto) {

        authService.signup(signupRequestDto);
        return ResponseEntity.status(CREATED)
                             .build();
    }

    @PostMapping("/find/username")
    public ResponseEntity<Boolean> existsUsername(@RequestBody UsernameRequestDto usernameRequestDto) {

        return ResponseEntity.status(OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.existsUsername(usernameRequestDto.getUsername()));
    }

    @PostMapping("/find/email")
    public ResponseEntity<Boolean> existsUsername(@RequestBody EmailRequestDto emailRequestDto) {

        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(authService.existsEmail(emailRequestDto.getEmail()));
    }

}

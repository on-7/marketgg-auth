package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Void> doSignup(@RequestBody SignupRequestDto signupRequestDto) {

        authService.signup(signupRequestDto);

        return ResponseEntity.status(CREATED)
                             .build();
    }
}

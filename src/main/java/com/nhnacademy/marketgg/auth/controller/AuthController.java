package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.nhnacademy.marketgg.auth.dto.EmailRequestDto;
import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.dto.UsernameRequestDto;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/find/username")
    public ResponseEntity<Boolean> existsUsername(
        @RequestBody UsernameRequestDto usernameRequestDto) {

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

    @GetMapping("/refresh")
    public ResponseEntity<Void> renewToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        HttpStatus httpStatus = OK;

        String newToken = null;
        if (Objects.isNull(authorizationHeader) ||
            (newToken = authService.renewToken(authorizationHeader.substring(7))) == null) {
            httpStatus = UNAUTHORIZED;
        }

        HttpHeaders headers = new HttpHeaders();

        if (Objects.nonNull(newToken)) {
            headers.setBearerAuth(newToken);
        }

        return ResponseEntity.status(httpStatus)
                             .headers(headers)
                             .build();
    }

}

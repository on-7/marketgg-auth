package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignupRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.util.Objects;
import javax.management.relation.RoleNotFoundException;
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
    public ResponseEntity<Void> doSignup(@RequestBody final SignupRequest signupRequest)
        throws RoleNotFoundException {
        authService.signup(signupRequest);
        return ResponseEntity.status(CREATED)
                             .build();
    }

    @PostMapping("/check/email")
    public ResponseEntity<EmailResponse> checkEmail(@RequestBody EmailRequest emailRequest) throws Exception {
    }

    @PostMapping("/find/username")
    public ResponseEntity<Boolean> existsUsername(@RequestBody UsernameRequest usernameRequest) {

        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(authService.existsUsername(usernameRequest.getUsername()));
    }

    @PostMapping("/find/email")
    public ResponseEntity<Boolean> existsUsername(@RequestBody EmailRequest emailRequest) {

        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(authService.checkEmail(emailRequest.getEmail()));
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

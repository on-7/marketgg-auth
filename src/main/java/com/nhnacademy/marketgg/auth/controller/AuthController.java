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
import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignupRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.relation.RoleNotFoundException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 로그인 요청을 받아 로그인을 진행합니다.
     *
     * @param loginRequest - 로그인에 필요한 요청 정보 객체
     * @return 로그인 성공/실패 여부가 담긴 ResponseEntity
     */
    @PostMapping("/login")
    public ResponseEntity<Void> doLogin(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);

        return ResponseEntity.status(HttpStatus.OK)
                             .headers(header -> header.setBearerAuth(token))
                             .build();
    }

    /**
     * 회원가입 요청을 받아 회원가입을 진행합니다.
     *
     * @param signUpRequest - 회원가입에 필요한 요청 정보 객체
     * @return 회원가입 성공/실패 여부가 담긴 ResponseEntity
     * @throws RoleNotFoundException - 역할을 부여받지 않거나, 읽을 수 없는 경우 예외 발생
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> doSignUp(@RequestBody final SignUpRequest signUpRequest) throws RoleNotFoundException {
        authService.signup(signUpRequest);

        return ResponseEntity.status(CREATED)
                             .build();
    }

    /**
     * 요청한 이메일이 유효한지 확인합니다.
     *
     * @param emailRequest - 이메일 로그인 요청 정보 객체
     * @return 이메일로 로그인 요청 성공/실패 여부가 담긴 ResponseEntity
     * @throws Exception - 예외 발생
     */
    @PostMapping("/check/email")
    public ResponseEntity<EmailResponse> checkEmail(@RequestBody EmailRequest emailRequest) throws Exception {

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

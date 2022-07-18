package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.service.AuthService;
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

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * 인증 관련 요청을 처리하는 Controller 입니다.
 *
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final int HEADER_BEARER = 7;

    private final AuthService authService;

    /**
     * 회원가입 요청을 받아 회원가입을 진행합니다.
     *
     * @param signUpRequest - 회원가입에 필요한 요청 정보 객체
     * @return 회원가입 성공/실패 여부가 담긴 ResponseEntity
     * @throws RoleNotFoundException - 역할을 부여받지 않거나, 읽을 수 없는 경우 예외 발생
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> doSignup(@RequestBody final SignUpRequest signUpRequest)
            throws RoleNotFoundException {

        authService.signup(signUpRequest);
        return ResponseEntity.status(CREATED)
                             .build();
    }

    /**
     * 요청한 이메일이 유효한지 확인합니다.
     *
     * @param emailRequest - 이메일 로그인 요청 정보 객체
     * @return 이메일로 로그인 요청 성공/실패 여부가 담긴 ResponseEntity
     */
    @PostMapping("/check/email")
    public ResponseEntity<EmailResponse> checkEmail(@RequestBody final EmailRequest emailRequest) {
        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(authService.checkEmail(emailRequest.getEmail()));
    }

    /**
     * JWT 를 갱신 요청합니다.
     *
     * @param request - Http 헤더에 JWT 토큰을 담아 요청을 전달합니다.
     * @return 요청 결과를 반환합니다.
     */
    @GetMapping("/refresh")
    public ResponseEntity<Void> renewToken(final HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        HttpStatus httpStatus = OK;

        String newToken = null;
        if (authorizationHeader.isBlank()
            || (newToken = authService.renewToken(authorizationHeader.substring(7))) == null) {
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

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Objects.nonNull(authorizationHeader)) {
            authService.logout(authorizationHeader.substring(HEADER_BEARER));
        }

        return ResponseEntity.status(OK)
                             .build();
    }

}

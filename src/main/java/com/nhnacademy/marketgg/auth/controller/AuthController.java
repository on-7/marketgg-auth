package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.nhnacademy.marketgg.auth.annotation.Token;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증을 담당하는 컨트롤러 클래스입니다.
 *
 * @version 1.0.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * JWT 를 갱신 요청합니다.
     *
     * @param token - 검증된 JWT
     * @return 요청 결과를 반환합니다.
     */
    @GetMapping("/refresh")
    public ResponseEntity<Void> renewToken(@Token String token) {

        HttpStatus httpStatus = OK;

        TokenResponse newToken = authService.renewToken(token);

        if (Objects.isNull(newToken)) {
            httpStatus = UNAUTHORIZED;
        }

        HttpHeaders headers = new HttpHeaders();

        if (Objects.nonNull(newToken)) {
            headers.setBearerAuth(newToken.getJwt());
            headers.set(TokenUtils.JWT_EXPIRE, newToken.getExpiredDate().toString());
        }

        return ResponseEntity.status(httpStatus)
                             .headers(headers)
                             .build();
    }

    /**
     * 회원이 로그아웃 요청 시 실행되는 메서드입니다.
     *
     * @param token - 검증된 JWT
     * @return 로그아웃이 완료되었다는 뜻으로 200 OK 를 응답합니다.
     */
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(@Token String token) {
        authService.logout(token);

        return ResponseEntity.status(OK)
                             .build();
    }

}

package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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

        TokenResponse newToken = null;
        if (authorizationHeader.isBlank()
                || ((newToken =
                authService.renewToken(authorizationHeader.substring(TokenUtils.BEARER_LENGTH)))
                == null)) {
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
     * @param request - 회원의 요청정보입니다.
     * @return 로그아웃이 완료되었다는 뜻으로 200 OK 를 응답합니다.
     */
    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (Objects.nonNull(authorizationHeader)) {
            authService.logout(authorizationHeader.substring(TokenUtils.BEARER_LENGTH));
        }

        return ResponseEntity.status(OK)
                             .build();
    }

}

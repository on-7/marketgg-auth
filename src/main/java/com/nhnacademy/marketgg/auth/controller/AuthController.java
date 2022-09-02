package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.nhnacademy.marketgg.auth.annotation.Token;
import com.nhnacademy.marketgg.auth.dto.response.common.AuthResult;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.TokenResponse;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증을 담당하는 컨트롤러 클래스입니다.
 *
 * @author 윤동열
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * JWT 를 갱신 요청합니다.
     *
     * @param request - 사용자 요청 정보
     * @return 요청 결과를 반환합니다.
     */
    @Operation(summary = "JWT 갱신",
        description = "JWT 갱신을 수행합니다.",
        parameters = @Parameter(description = "Http 요청 헤더", required = true),
        responses = @ApiResponse(responseCode = "200",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AuthResult.class))))
    @GetMapping("/token/refresh")
    public ResponseEntity<AuthResult<String>> renewToken(HttpServletRequest request) {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Objects.isNull(jwt)) {
            throw new UnAuthorizationException();
        }

        if (jwt.startsWith(TokenUtils.BEARER)) {
            jwt = jwt.substring(TokenUtils.BEARER_LENGTH);
        }

        HttpStatus httpStatus = OK;

        TokenResponse newToken = authService.renewToken(jwt);

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
                             .body(AuthResult.success("Login Success"));
    }

    /**
     * 회원이 로그아웃 요청 시 실행되는 메서드입니다.
     *
     * @param token - 검증된 JWT
     * @return 로그아웃이 완료되었다는 뜻으로 200 OK 를 응답합니다.
     */
    @Operation(summary = "로그아웃",
        description = "로그아웃",
        parameters = @Parameter(description = "JWT 토큰", required = true),
        responses = @ApiResponse(responseCode = "200",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = AuthResult.class))))
    @GetMapping("/logout")
    public ResponseEntity<AuthResult<String>> logout(@Token String token) {
        authService.logout(token);

        return ResponseEntity.status(OK)
                             .body(AuthResult.success("Logout Success"));
    }

}

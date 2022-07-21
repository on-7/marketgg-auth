package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.EmailUseRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.SignUpResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.dto.response.ExistEmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.UseEmailResponse;
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

    private final AuthService authService;

    /**
     * 회원가입 요청을 받아 회원가입을 진행합니다.
     *
     * @param signUpRequest - 회원가입에 필요한 요청 정보 객체
     * @return 회원가입 성공/실패 여부가 담긴 ResponseEntity
     * @throws RoleNotFoundException - 역할을 부여받지 않거나, 읽을 수 없는 경우 예외 발생
     */
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> doSignup(@RequestBody final SignUpRequest signUpRequest)
        throws RoleNotFoundException {


        return ResponseEntity.status(CREATED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(authService.signup(signUpRequest));
    }

    /**
     * 요청한 이메일이 중복되는지 확인합니다.
     *
     * @param emailRequest - 이메일 로그인 요청 정보 객체
     * @return 이메일로 로그인 요청 존재하는 이메일인지 성공/실패 여부가 담긴 ResponseEntity
     */
    @PostMapping("/check/email")
    public ResponseEntity<ExistEmailResponse> checkEmail(@RequestBody final EmailRequest emailRequest) {
        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(authService.checkEmail(emailRequest));
    }

    /**
     * 요청한 이메일이 중복되는지 확인합니다.
     *
     * @param emailUseRequest - 이메일 로그인 요청 정보 객체
     * @return 이메일로 로그인 요청 사용할 수 있는 이메일인지 성공/실패 여부가 담긴 ResponseEntity
     */
    @PostMapping("/use/email")
    public ResponseEntity<UseEmailResponse> useEmail(@RequestBody final EmailUseRequest emailUseRequest) {
        return ResponseEntity.status(OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.useEmail(emailUseRequest));
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

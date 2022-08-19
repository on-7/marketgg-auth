package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.response.GoogleProfile;
import com.nhnacademy.marketgg.auth.dto.response.OauthLoginResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.AuthResult;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.impl.GoogleLoginService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Google 로그인 요청시 사용되는 클래스입니다.
 *
 * @author 윤동열
 */
@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class GoogleLoginController {

    private final GoogleLoginService googleLoginService;

    /**
     * Google 로그인 요청 시 처리합니다.
     *
     * @param request - 구글에 요청을 보낼 때 필요한 정보들.
     * @return 프로필 정보 또는 JWT
     */
    @PostMapping("/login/google")
    public ResponseEntity<AuthResult<GoogleProfile>> oauthLogin(@RequestBody Map<String, String> request) {
        OauthLoginResponse loginResponse = googleLoginService.requestProfile(request.get("code"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status;
        GoogleProfile data;

        if (loginResponse.successLogin()) {
            TokenResponse tokenResponse = loginResponse.getTokenResponse();

            headers.setBearerAuth(tokenResponse.getJwt());
            headers.set(TokenUtils.JWT_EXPIRE, tokenResponse.getExpiredDate().toString());

            // 로그인 성공 시 프로필 없이 JWT 만 응답으로 보내기 때문에 Profile 정보가 없다.
            data = GoogleProfile.successGoogleLogin();
            status = HttpStatus.OK;
        } else {
            data = (GoogleProfile) loginResponse.getOauthProfile();
            status = HttpStatus.UNAUTHORIZED;
        }

        return ResponseEntity.status(status)
                             .headers(headers)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(AuthResult.success(data));
    }

}

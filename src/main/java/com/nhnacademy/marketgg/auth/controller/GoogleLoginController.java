package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.response.GoogleProfile;
import com.nhnacademy.marketgg.auth.dto.response.OauthResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.CommonResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.SingleResponse;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * Google 로그인 요청시 사용되는 클래스입니다.
 *
 * @author 윤동열
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class GoogleLoginController {

    private final GoogleLoginService googleLoginService;

    /**
     * 구글 요청 시 실행됩니다.
     * @param request - 요청 정보
     * @return 로그인 성공 시 JWT 를 반환하고 로그인 실패 시 사용자의 Email, 이름으로 회원가입 합니다.
     */
    @PostMapping("/login/google")
    public ResponseEntity<CommonResponse> oauthLogin(@RequestBody Map<String, String> request) {
        GoogleProfile googleProfile = googleLoginService.requestProfile(request.get("code"));

        if (googleProfile.isSuccess()) {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(googleProfile.getTokenResponse().getJwt());
            headers.set(TokenUtils.JWT_EXPIRE, googleProfile.getTokenResponse().getExpiredDate().toString());

            return ResponseEntity.status(HttpStatus.OK)
                                 .headers(headers)
                                 .body(new SingleResponse<>("Login Success"));
        }

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(new SingleResponse<>(googleProfile));
    }

}

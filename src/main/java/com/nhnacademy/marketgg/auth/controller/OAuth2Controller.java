package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.response.GoogleProfile;
import com.nhnacademy.marketgg.auth.dto.response.OauthResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.CommonResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.SingleResponse;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.OAuth2Service;
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

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @PostMapping("/login/google")
    public ResponseEntity<CommonResponse> oauthLogin(@RequestBody Map<String, String> request) {
        OauthResponse oauthResponse = oAuth2Service.requestProfile(request.get("code"));

        if (oauthResponse.isSuccess()) {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(oauthResponse.getTokenResponse().getJwt());
            headers.set(TokenUtils.JWT_EXPIRE, oauthResponse.getTokenResponse().getExpiredDate().toString());

            return ResponseEntity.status(HttpStatus.OK)
                                 .headers(headers)
                                 .body(new SingleResponse<>("Login Success"));
        }

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(new SingleResponse<>((GoogleProfile) oauthResponse));
    }

}

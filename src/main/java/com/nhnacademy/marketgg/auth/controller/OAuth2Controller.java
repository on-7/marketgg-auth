package com.nhnacademy.marketgg.auth.controller;

import com.nhnacademy.marketgg.auth.dto.response.GoogleProfile;
import com.nhnacademy.marketgg.auth.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @PostMapping("/auth/login/google")
    public ResponseEntity<GoogleProfile> oauthLogin(@RequestBody Map<String, String> request) {
        GoogleProfile code = oAuth2Service.requestProfile(request.get("code"));
        return ResponseEntity.status(HttpStatus.OK)
                             .body(code);
    }

}

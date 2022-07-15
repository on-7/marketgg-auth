package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.response.GoogleProfile;
import com.nhnacademy.marketgg.auth.exception.OAuthRequestFailException;
import com.nhnacademy.marketgg.auth.oauth2.OAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private static final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-key}")
    private String googleClientKey;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;

    public GoogleProfile requestProfile(String code) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("code", code);
        parameters.add("client_id", googleClientId);
        parameters.add("client_secret", googleClientKey);
        parameters.add("redirect_uri", redirectUri);
        parameters.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> tokenRequest
                = new HttpEntity<>(parameters, headers);

        ResponseEntity<OAuthToken> response
                = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL, tokenRequest, OAuthToken.class);

        String accessToken = Optional.ofNullable(response.getBody())
                                     .orElseThrow(OAuthRequestFailException::new)
                                     .getAccessToken();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                                                          .scheme("https")
                                                          .host("www.googleapis.com")
                                                          .path("oauth2/v1/userinfo")
                                                          .queryParam("access_token", accessToken)
                                                          .build(true);

        ResponseEntity<GoogleProfile> tokenResponse =
                restTemplate.getForEntity(uriComponents.toUri(), GoogleProfile.class);

        return tokenResponse.getBody();
    }

}

package com.nhnacademy.marketgg.auth.adapter;

import com.nhnacademy.marketgg.auth.oauth2.OAuthToken;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Google 에 소셜 로그인을 요청하는 구현체.
 *
 * @author 윤동열
 */
@Component
@RequiredArgsConstructor
public class GoogleAdapter implements OauthAdapter {

    private final RestTemplate restTemplate;

    @Override
    public ResponseEntity<OAuthToken> requestToken(String requestUrl,
                                                   HttpEntity<MultiValueMap<String, String>> params) {

        return restTemplate.postForEntity(requestUrl, params, OAuthToken.class);
    }

    @Override
    public <T> ResponseEntity<T> requestProfile(URI requestUri, Class<T> clazz) {

        return restTemplate.getForEntity(requestUri, clazz);
    }

}

package com.nhnacademy.marketgg.auth.adapter;

import com.nhnacademy.marketgg.auth.oauth2.OAuthToken;
import java.net.URI;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

/**
 * 소셜 로그인 벤더와 통신하는 Adapter 클래스.
 *
 * @author 윤동열
 */
public interface OauthAdapter {

    ResponseEntity<OAuthToken> requestToken(String requestUrl, HttpEntity<MultiValueMap<String, String>> params);

    <T> ResponseEntity<T> requestProfile(URI requestUri, Class<T> clazz);

}

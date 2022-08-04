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

    /**
     * 소셜 로그인 벤더에 Access Token 을 요청합니다.
     *
     * @param requestUrl - 요청 URL
     * @param params     - 요청 시 필요한 Parameter (client id, client secret, ...)
     * @return Access Token
     */
    ResponseEntity<OAuthToken> requestToken(String requestUrl, HttpEntity<MultiValueMap<String, String>> params);

    /**
     * Access Token 으로 사용자의 프로필 정보를 요청합니다.
     *
     * @param requestUri - 사용자 프로필을 요청할 URI
     * @param clazz      - 반환받을 클래스 타입
     * @param <T>        - 소셜로그인 벤더
     * @return 사용자 프로필
     */
    <T> ResponseEntity<T> requestProfile(URI requestUri, Class<T> clazz);

}

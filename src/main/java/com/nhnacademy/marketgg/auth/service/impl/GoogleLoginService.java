package com.nhnacademy.marketgg.auth.service.impl;

import static java.util.stream.Collectors.toList;

import com.nhnacademy.marketgg.auth.adapter.GoogleAdapter;
import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.OauthLoginResponse;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.TokenResponse;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.google.GoogleProfile;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import com.nhnacademy.marketgg.auth.exception.OAuthRequestFailException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.oauth2.OAuthToken;
import com.nhnacademy.marketgg.auth.repository.auth.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.role.RoleRepository;
import com.nhnacademy.marketgg.auth.service.Oauth2Service;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 구글 로그인 처리를 담당하는 클래스입니다.
 *
 * @author 윤동열
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleLoginService implements Oauth2Service {

    private static final String GOOGLE_TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";

    @Value("${gg.google.client-id}")
    private String googleClientId;

    @Value("${gg.google.client-key}")
    private String googleClientKey;

    @Value("${gg.google.redirect-uri}")
    private String redirectUri;

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final TokenUtils tokenUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final GoogleAdapter googleAdapter;

    /**
     * {@inheritDoc}
     */
    @Override
    public OauthLoginResponse requestProfile(String code) {

        OAuthToken tokenResponse = this.requestAccessToken(code);

        GoogleProfile googleProfile = this.requestGoogleProfile(tokenResponse);
        log.info("Google Profile = {}", googleProfile);

        Optional<Auth> opAuth = authRepository.findByEmailAndProvider(googleProfile.getEmail(), Provider.GOOGLE)
                                              .filter(Auth::isMember);

        if (opAuth.isEmpty()) {
            // DB 에 회원 정보가 없을 시 로그인 시도한 프로필을 바탕으로 회원가입 진행
            return OauthLoginResponse.doSignUp(googleProfile);
        }

        Auth auth = opAuth.get();

        List<SimpleGrantedAuthority> roles = roleRepository.findRolesByAuthId(auth.getId())
                                                           .stream()
                                                           .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                                                           .collect(toList());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(auth.getUuid(), "", roles);

        TokenResponse jwtResponse = tokenUtils.saveRefreshToken(redisTemplate, token);

        // 로그인 성공 시 JWT 반환
        return OauthLoginResponse.loginSuccess(jwtResponse);
    }

    private OAuthToken requestAccessToken(String code) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("client_id", googleClientId);
        map.add("client_secret", googleClientKey);
        map.add("redirect_uri", redirectUri);
        map.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> tokenRequest
                = new HttpEntity<>(map, headers);

        return Optional.ofNullable(googleAdapter.requestToken(GOOGLE_TOKEN_REQUEST_URL, tokenRequest).getBody())
                       .orElseThrow(LoginFailException::new);
    }

    private GoogleProfile requestGoogleProfile(OAuthToken tokenResponse) {
        String accessToken = Optional.ofNullable(tokenResponse)
                                     .orElseThrow(OAuthRequestFailException::new)
                                     .getAccessToken();

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                                                          .scheme("https")
                                                          .host("www.googleapis.com")
                                                          .path("oauth2/v1/userinfo")
                                                          .queryParam("access_token", accessToken)
                                                          .build(true);

        return Optional.ofNullable(googleAdapter.requestProfile(uriComponents.toUri(), GoogleProfile.class).getBody())
                       .orElseThrow(LoginFailException::new);
    }

}

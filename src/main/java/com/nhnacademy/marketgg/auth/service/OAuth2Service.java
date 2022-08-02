package com.nhnacademy.marketgg.auth.service;

import static java.util.stream.Collectors.toList;

import com.nhnacademy.marketgg.auth.dto.request.GoogleProfileRequest;
import com.nhnacademy.marketgg.auth.dto.response.GoogleProfile;
import com.nhnacademy.marketgg.auth.dto.response.OauthResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import com.nhnacademy.marketgg.auth.exception.OAuthRequestFailException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.oauth2.OAuthToken;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

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
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public OauthResponse requestProfile(String code) {
        GoogleProfileRequest profileRequest =
            new GoogleProfileRequest(code, googleClientId, googleClientKey, redirectUri);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<GoogleProfileRequest> tokenRequest
            = new HttpEntity<>(profileRequest, headers);

        ResponseEntity<OAuthToken> response
            = restTemplate.postForEntity(GOOGLE_TOKEN_REQUEST_URL, tokenRequest, OAuthToken.class);

        if (Objects.isNull(response.getBody())) {
            throw new LoginFailException();
        }

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

        GoogleProfile googleProfile = tokenResponse.getBody();

        if (Objects.isNull(googleProfile)) {
            throw new LoginFailException();
        }

        Auth auth = authRepository.findByEmail(googleProfile.getEmail())
                                  .orElse(null);

        if (Objects.isNull(auth)) {
            return new GoogleProfile(googleProfile.getEmail(), googleProfile.getName());
        }

        List<SimpleGrantedAuthority> roles = roleRepository.findRolesByAuthId(auth.getId())
                                                           .stream()
                                                           .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                                                           .collect(toList());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(auth.getUuid(), "", roles);

        TokenResponse jwtResponse = tokenUtils.saveRefreshToken(redisTemplate, token);

        return new GoogleProfile(true, jwtResponse);
    }

}

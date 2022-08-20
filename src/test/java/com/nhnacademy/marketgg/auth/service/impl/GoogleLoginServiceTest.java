package com.nhnacademy.marketgg.auth.service.impl;

import static com.nhnacademy.marketgg.auth.constant.Roles.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import com.nhnacademy.marketgg.auth.adapter.GoogleAdapter;
import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.dto.request.signup.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.google.GoogleProfile;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.OauthLoginResponse;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.TokenResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.oauth2.OAuthToken;
import com.nhnacademy.marketgg.auth.repository.auth.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.role.RoleRepository;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class GoogleLoginServiceTest {

    @InjectMocks
    GoogleLoginService googleLoginService;

    @Mock
    AuthRepository authRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    TokenUtils tokenUtils;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Mock
    GoogleAdapter googleAdapter;

    @Test
    @DisplayName("구글 프로필 요청")
    void testRequestProfile() {
        String code = "code";
        OAuthToken oAuthToken = new OAuthToken();

        String email = "email@gmail.com";
        String name = "홍길동";
        GoogleProfile googleProfile = new GoogleProfile();
        ReflectionTestUtils.setField(googleProfile, "email", email);
        ReflectionTestUtils.setField(googleProfile, "name", name);

        Auth auth = spy(new Auth(getSignUpRequest()));

        String jwt = "jwt";
        LocalDateTime now = LocalDateTime.now();
        TokenResponse tokenResponse = new TokenResponse(jwt, now);

        given(auth.getId()).willReturn(1L);

        given(googleAdapter.requestToken(anyString(), any(HttpEntity.class)))
            .willReturn(ResponseEntity.of(Optional.of(oAuthToken)));
        given(googleAdapter.requestProfile(any(URI.class), any()))
            .willReturn(ResponseEntity.of(Optional.of(googleProfile)));

        given(authRepository.findByEmailAndProvider(googleProfile.getEmail(), Provider.GOOGLE)).willReturn(Optional.of(auth));
        given(roleRepository.findRolesByAuthId(auth.getId())).willReturn(List.of(new Role(ROLE_USER)));
        given(tokenUtils.saveRefreshToken(any(redisTemplate.getClass()), any(Authentication.class)))
            .willReturn(tokenResponse);

        OauthLoginResponse loginResponse = googleLoginService.requestProfile(code);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.getOauthProfile()).isNull();

        then(googleAdapter).should(times(1)).requestToken(anyString(), any());
        then(googleAdapter).should(times(1)).requestProfile(any(URI.class), any());
        then(tokenUtils).should(times(1)).saveRefreshToken(any(), any(Authentication.class));
    }

    private SignUpRequest getSignUpRequest() {
        SignUpRequest signUpRequest = new SignUpRequest();

        ReflectionTestUtils.setField(signUpRequest, "email", "email@gmail.com");
        ReflectionTestUtils.setField(signUpRequest, "password", UUID.randomUUID().toString());
        ReflectionTestUtils.setField(signUpRequest, "name", "name");
        ReflectionTestUtils.setField(signUpRequest, "phoneNumber", "01012341234");
        ReflectionTestUtils.setField(signUpRequest, "provider", "GOOGLE");

        return signUpRequest;
    }


}

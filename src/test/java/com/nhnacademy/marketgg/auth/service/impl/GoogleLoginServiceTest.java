package com.nhnacademy.marketgg.auth.service.impl;

import static com.nhnacademy.marketgg.auth.constant.Roles.ROLE_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.nhnacademy.marketgg.auth.adapter.GoogleAdapter;
import com.nhnacademy.marketgg.auth.dto.response.GoogleProfile;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.oauth2.OAuthToken;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
        GoogleProfile googleProfile = new GoogleProfile("email@gmail.com", "홍길동");
        Auth auth = mock(Auth.class);
        String jwt = "jwt";
        LocalDateTime now = LocalDateTime.now();
        TokenResponse tokenResponse = new TokenResponse(jwt, now);

        given(auth.getId()).willReturn(1L);
        given(googleAdapter.requestToken(anyString(), any(HttpEntity.class)))
            .willReturn(ResponseEntity.of(Optional.of(oAuthToken)));
        given(googleAdapter.requestProfile(any(URI.class), any()))
            .willReturn(ResponseEntity.of(Optional.of(googleProfile)));
        given(authRepository.findByEmail(googleProfile.getEmail())).willReturn(Optional.of(auth));
        given(roleRepository.findRolesByAuthId(auth.getId())).willReturn(List.of(new Role(ROLE_USER)));
        given(tokenUtils.saveRefreshToken(any(redisTemplate.getClass()), any(Authentication.class)))
            .willReturn(tokenResponse);

        GoogleProfile googleProfile1 = (GoogleProfile) googleLoginService.requestProfile(code);

        assertThat(googleProfile1).isNotNull();
        assertThat(googleProfile1.getEmail()).isNull();
        assertThat(googleProfile1.getName()).isNull();

        then(googleAdapter).should(times(1)).requestToken(anyString(), any());
        then(googleAdapter).should(times(1)).requestProfile(any(URI.class), any());
        then(tokenUtils).should(times(1)).saveRefreshToken(any(), any(Authentication.class));
    }

}

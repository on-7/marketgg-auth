package com.nhnacademy.marketgg.auth.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class DefaultAuthServiceTest {

    @InjectMocks
    private DefaultAuthService defaultAuthService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private TokenGenerator tokenGenerator;

    @DisplayName("로그인 시 JWT 발급")
    @Test
    void testLogin() {
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "username", "username");
        ReflectionTestUtils.setField(loginRequest, "password", "password");

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);

        String jwt = "jwt";
        String refreshToken = "refreshToken";

        when(tokenGenerator.generateJwt(authentication)).thenReturn(jwt);
        when(tokenGenerator.generateRefreshToken(authentication)).thenReturn(refreshToken);

        HashOperations ho = mock(HashOperations.class);
        when(redisTemplate.opsForHash()).thenReturn(ho);

        doNothing().when(ho)
                   .put(loginRequest.getUsername(), "refresh_token", refreshToken);

        Assertions.assertThat(defaultAuthService.login(loginRequest)).isEqualTo(jwt);
    }

}
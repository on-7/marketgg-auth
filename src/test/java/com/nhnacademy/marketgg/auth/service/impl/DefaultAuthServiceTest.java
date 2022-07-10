package com.nhnacademy.marketgg.auth.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class DefaultAuthServiceTest {

    @InjectMocks
    private DefaultAuthService authService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private TokenGenerator tokenGenerator;

    @Test
    @DisplayName("회원가입 테스트")
    void testSignup() {

        SignupRequestDto testSignupRequestDto = new SignupRequestDto();

        ReflectionTestUtils.setField(testSignupRequestDto, "username", "testUsername");
        ReflectionTestUtils.setField(testSignupRequestDto, "password", "1234");
        ReflectionTestUtils.setField(testSignupRequestDto, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignupRequestDto, "name", "testName");

        Auth auth = new Auth(testSignupRequestDto);

        given(authRepository.save(any())).willReturn(auth);

        authService.signup(testSignupRequestDto);

        verify(authRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("회원 아이디 중복체크")
    void testExistsUsername() {

        given(authRepository.existsByUsername(any())).willReturn(true);

        authService.existsUsername("testUsername");

        verify(authRepository, times(1)).existsByUsername(any());
    }


    @Test
    @DisplayName("회원 이메일 중복체크")
    void testExistsEmail() {

        given(authRepository.existsByEmail(any())).willReturn(true);

        authService.existsEmail("test@test.com");

        verify(authRepository, times(1)).existsByEmail(any());
    }

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

        Assertions.assertThat(authService.login(loginRequest)).isEqualTo(jwt);
    }

}
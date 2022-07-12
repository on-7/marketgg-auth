package com.nhnacademy.marketgg.auth.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
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

}
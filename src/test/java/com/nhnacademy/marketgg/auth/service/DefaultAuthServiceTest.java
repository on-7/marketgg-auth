package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class DefaultAuthServiceTest {

    @Mock
    private AuthService authService;
    @Mock
    private AuthRepository authRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        authService = new DefaultAuthService(authRepository,passwordEncoder);
    }

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
}
package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.entity.Auth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepository;

    @DisplayName("회원가입 테스트")
    @Test
    void testSignup() {

        SignupRequestDto testSignupRequestDto = new SignupRequestDto();

        ReflectionTestUtils.setField(testSignupRequestDto, "username", "testUsername");
        ReflectionTestUtils.setField(testSignupRequestDto, "password", "1234");
        ReflectionTestUtils.setField(testSignupRequestDto, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignupRequestDto, "name", "testName");

        Auth testAuth = new Auth(testSignupRequestDto);

        Auth save = authRepository.save(testAuth);

        assertThat(save).isEqualTo(testAuth);

    }

}
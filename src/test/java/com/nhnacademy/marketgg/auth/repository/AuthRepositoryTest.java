package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.entity.Auth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class AuthRepositoryTest {

    @Autowired
    AuthRepository authRepository;

    SignUpRequest testSignUpRequest;

    @BeforeEach
    void setUp() {
        testSignUpRequest = new SignUpRequest();

        ReflectionTestUtils.setField(testSignUpRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignUpRequest, "password", "1234");
        ReflectionTestUtils.setField(testSignUpRequest, "name", "testName");
        ReflectionTestUtils.setField(testSignUpRequest, "phoneNumber", "01087654321");
    }

    @DisplayName("회원가입 테스트")
    @Test
    void testSignup() {
        // Given
        Auth testAuth = new Auth(testSignUpRequest);

        // When
        Auth save = authRepository.save(testAuth);

        // Then
        assertThat(save).isEqualTo(testAuth);
    }

    @DisplayName("회원 이메일 중복체크")
    @Test
    void testExistsEmail() {
        // Given
        Auth testAuth = new Auth(testSignUpRequest);

        EmailRequest testEmailRequest = new EmailRequest();

        // When
        Auth save = authRepository.save(testAuth);

        ReflectionTestUtils.setField(testEmailRequest, "email", "test@test.com");

        Boolean isExistsEmail = authRepository.existsByEmail("test@test.com");

        // Then
        assertThat(save).isEqualTo(testAuth);
        assertThat(isExistsEmail).isTrue();
    }

}

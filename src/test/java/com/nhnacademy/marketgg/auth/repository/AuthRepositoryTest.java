package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignupRequest;
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
    private AuthRepository authRepository;

    private SignupRequest testSignupRequest;

    @BeforeEach
    void setUp() {

        testSignupRequest = new SignupRequest();

        ReflectionTestUtils.setField(testSignupRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignupRequest, "password", "1234");
        ReflectionTestUtils.setField(testSignupRequest, "name", "testName");

    }

    @DisplayName("회원가입 테스트")
    @Test
    void testSignup() {

        //given
        Auth testAuth = new Auth(testSignupRequest);

        //when
        Auth save = authRepository.save(testAuth);

        //then
        assertThat(save).isEqualTo(testAuth);
    }

    @DisplayName("회원 이메일 중복체크")
    @Test
    void testExistsEmail() {

        //given
        Auth testAuth = new Auth(testSignupRequest);

        EmailRequest testEmailRequest = new EmailRequest();

        //when
        Auth save = authRepository.save(testAuth);

        ReflectionTestUtils.setField(testEmailRequest, "email", "test@test.com");

        Boolean isExistsEmail = authRepository.existsByEmail("test@test.com");

        //then
        assertThat(save).isEqualTo(testAuth);
        assertThat(isExistsEmail).isTrue();

    }



}
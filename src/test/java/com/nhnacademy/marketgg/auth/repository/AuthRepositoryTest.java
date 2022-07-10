package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.dto.EmailRequestDto;
import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.dto.UsernameRequestDto;
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

    private SignupRequestDto testSignupRequestDto;

    @BeforeEach
    void setUp() {

        testSignupRequestDto = new SignupRequestDto();

        ReflectionTestUtils.setField(testSignupRequestDto, "username", "testUsername");
        ReflectionTestUtils.setField(testSignupRequestDto, "password", "1234");
        ReflectionTestUtils.setField(testSignupRequestDto, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignupRequestDto, "name", "testName");

    }

    @DisplayName("회원가입 테스트")
    @Test
    void testSignup() {

        //given
        Auth testAuth = new Auth(testSignupRequestDto);

        //when
        Auth save = authRepository.save(testAuth);

        //then
        assertThat(save).isEqualTo(testAuth);
    }

    @DisplayName("회원 아이디 중복체크")
    @Test
    void testExistsUsername() {

        //given
        Auth testAuth = new Auth(testSignupRequestDto);

        UsernameRequestDto testUsernameRequestDto = new UsernameRequestDto();

        //when
        Auth save = authRepository.save(testAuth);

        ReflectionTestUtils.setField(testUsernameRequestDto, "username", "testUsername");

        Boolean isExistsUsername = authRepository.existsByUsername("testUsername");

        //then
        assertThat(save).isEqualTo(testAuth);
        assertThat(isExistsUsername).isTrue();

    }

    @DisplayName("회원 이메일 중복체크")
    @Test
    void testExistsEmail() {

        //given
        Auth testAuth = new Auth(testSignupRequestDto);

        EmailRequestDto testEmailRequestDto = new EmailRequestDto();

        //when
        Auth save = authRepository.save(testAuth);

        ReflectionTestUtils.setField(testEmailRequestDto, "email", "test@test.com");

        Boolean isExistsEmail = authRepository.existsByEmail("test@test.com");

        //then
        assertThat(save).isEqualTo(testAuth);
        assertThat(isExistsEmail).isTrue();

    }



}
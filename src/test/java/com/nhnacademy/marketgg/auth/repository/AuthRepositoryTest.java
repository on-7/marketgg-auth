package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.dto.request.signup.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.signup.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    @DisplayName("UUID 리스트로 회원 목록 조회")
    void testFindMembersByUuid() {

        List<String> uuids = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Auth savedAuth = authRepository.save(new Auth(getSignUpRequest(i)));
            uuids.add(savedAuth.getUuid());
        }

        List<MemberNameResponse> membersByUuid = authRepository.findMembersByUuid(uuids);

        assertThat(membersByUuid).hasSize(10);
    }

    @DisplayName("소셜로그인 ")
    @Test
    void testFindByEmailAndProvider() {
        Auth auth = new Auth(getSignUpRequest(100));
        Auth savedAuth = authRepository.save(auth);
        Auth findAuth = authRepository.findByEmailAndProvider(auth.getEmail(), Provider.GOOGLE)
                                      .orElseThrow();

        Assertions.assertAll(
            () -> assertThat(findAuth.getEmail()).isEqualTo(savedAuth.getEmail()),
            () -> assertThat(findAuth.getProvider()).isEqualTo(savedAuth.getProvider()));
    }

    private SignUpRequest getSignUpRequest(int i) {
        SignUpRequest signUpRequest = new SignUpRequest();

        ReflectionTestUtils.setField(signUpRequest, "email", "email" + i + "@gmail.com");
        ReflectionTestUtils.setField(signUpRequest, "password",UUID.randomUUID().toString());
        ReflectionTestUtils.setField(signUpRequest, "name", "name" + i);
        ReflectionTestUtils.setField(signUpRequest, "phoneNumber", "01012341234");
        ReflectionTestUtils.setField(signUpRequest, "provider", i % 2 == 0 ?"GOOGLE" : "SELF");

        return signUpRequest;
    }

}

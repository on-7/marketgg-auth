package com.nhnacademy.marketgg.auth.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nhnacademy.marketgg.auth.config.WebSecurityConfig;
import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.EmailUseRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.AuthRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.AuthRoleRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import com.nhnacademy.marketgg.auth.util.MailUtils;
import com.nhnacademy.marketgg.auth.util.RedisUtils;
import java.util.Optional;
import javax.management.relation.RoleNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@Import({
    WebSecurityConfig.class
})
class DefaultSignUpServiceTest {

    @InjectMocks
    DefaultSignUpService defaultSignUpService;

    @InjectMocks
    DefaultAuthService defaultAuthService;

    @Mock
    AuthRepository authRepository;

    @Mock
    AuthRoleRepository authRoleRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    MailUtils mailUtils;

    @Mock
    RedisUtils redisUtils;

    @Mock
    PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("회원가입 테스트 추천인 X")
    void testSignup() throws RoleNotFoundException {
        SignUpRequest testSignUpRequest = new SignUpRequest();

        ReflectionTestUtils.setField(testSignUpRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignUpRequest, "password", "1234");
        ReflectionTestUtils.setField(testSignUpRequest, "name", "testName");
        ReflectionTestUtils.setField(testSignUpRequest, "phoneNumber", "01087654321");

        Auth auth = new Auth(testSignUpRequest);
        given(authRepository.save(any(Auth.class))).willReturn(auth);

        Long authNo = auth.getId();
        Role role = new Role(Roles.ROLE_USER);
        ReflectionTestUtils.setField(role, "id", 0L);
        given(roleRepository.findByName(Roles.ROLE_USER)).willReturn(Optional.of(role));

        AuthRole.Pk pk = new AuthRole.Pk(authNo, role.getId());
        AuthRole authRole = new AuthRole(pk, auth, role);

        given(authRoleRepository.save(any(AuthRole.class))).willReturn(authRole);

        defaultSignUpService.signup(testSignUpRequest);

        verify(authRepository, times(1)).save(any(auth.getClass()));
        // getDeclaringClass() 메서드는 이 클래스의 선언 클래스를 가져오는 데 사용됨.
        verify(roleRepository, times(1)).findByName(any(Roles.ROLE_USER.getDeclaringClass()));
        verify(authRoleRepository, times(1)).save(any(authRole.getClass()));
    }

    @Test
    @DisplayName("회원가입 테스트 추천인 O")
    void testSignupReferrer() throws RoleNotFoundException {

        SignUpRequest ReferrerSignupRequest = new SignUpRequest();

        ReflectionTestUtils.setField(ReferrerSignupRequest, "email", "test2@test.com");
        ReflectionTestUtils.setField(ReferrerSignupRequest, "password", "12345");
        ReflectionTestUtils.setField(ReferrerSignupRequest, "name", "testName3");
        ReflectionTestUtils.setField(ReferrerSignupRequest, "phoneNumber", "010876543212");

        Auth referrerAuth = new Auth(ReferrerSignupRequest);
        lenient().when(authRepository.save(any(Auth.class))).thenReturn(referrerAuth);

        Long referrerNo = referrerAuth.getId();
        Role referrerRole = new Role(Roles.ROLE_USER);
        ReflectionTestUtils.setField(referrerRole, "id", 0L);
        given(roleRepository.findByName(Roles.ROLE_USER)).willReturn(Optional.of(referrerRole));

        AuthRole.Pk referrerPk = new AuthRole.Pk(referrerNo, referrerRole.getId());
        AuthRole referrerAuthRole = new AuthRole(referrerPk, referrerAuth, referrerRole);

        lenient().when(authRoleRepository.save(any(AuthRole.class))).thenReturn(referrerAuthRole);

        SignUpRequest testSignUpRequest = new SignUpRequest();

        ReflectionTestUtils.setField(testSignUpRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignUpRequest, "password", "1234");
        ReflectionTestUtils.setField(testSignUpRequest, "name", "testName");
        ReflectionTestUtils.setField(testSignUpRequest, "phoneNumber", "01087654321");
        ReflectionTestUtils.setField(testSignUpRequest, "referrerEmail", "referrerEmail@referrerEmail.com");

        given(authRepository.existsByEmail(testSignUpRequest.getEmail())).willReturn(false);

        Auth auth = new Auth(testSignUpRequest);
        given(authRepository.save(any(Auth.class))).willReturn(auth);

        Long authNo = auth.getId();
        Role role = new Role(Roles.ROLE_USER);
        ReflectionTestUtils.setField(role, "id", 0L);
        given(roleRepository.findByName(Roles.ROLE_USER)).willReturn(Optional.of(role));

        AuthRole.Pk pk = new AuthRole.Pk(authNo, role.getId());
        AuthRole authRole = new AuthRole(pk, auth, role);

        given(authRoleRepository.save(any(AuthRole.class))).willReturn(authRole);
        given(authRepository.existsByEmail(testSignUpRequest.getReferrerEmail())).willReturn(true);
        given(authRepository.findByEmail(testSignUpRequest.getReferrerEmail())).willReturn(Optional.of(referrerAuth));

        defaultSignUpService.signup(testSignUpRequest);

        verify(authRepository, times(1)).save(any(auth.getClass()));
        // getDeclaringClass() 메서드는 이 클래스의 선언 클래스를 가져오는 데 사용됨.
        verify(roleRepository, times(1)).findByName(any(Roles.ROLE_USER.getDeclaringClass()));
        verify(authRoleRepository, times(1)).save(any(authRole.getClass()));
    }

    @Test
    @DisplayName("없는 권한으로 회원가입시 에러 처리")
    void testRoleNotFoundException() {
        SignUpRequest testSignUpRequest = new SignUpRequest();

        ReflectionTestUtils.setField(testSignUpRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignUpRequest, "password", "1234");
        ReflectionTestUtils.setField(testSignUpRequest, "name", "testName");
        ReflectionTestUtils.setField(testSignUpRequest, "phoneNumber", "01087654321");

        given(authRepository.existsByEmail(testSignUpRequest.getEmail())).willReturn(false);
        given(authRepository.save(any())).willReturn(new Auth(testSignUpRequest));

        assertThatThrownBy(() -> defaultSignUpService.signup(testSignUpRequest))
            .isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    @DisplayName("회원가입하는 회원의 이메일 중복 에러처리")
    void testSignupEmailOverlapException() {
        SignUpRequest testSignUpRequest = new SignUpRequest();

        ReflectionTestUtils.setField(testSignUpRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignUpRequest, "password", "1234");
        ReflectionTestUtils.setField(testSignUpRequest, "name", "testName");
        ReflectionTestUtils.setField(testSignUpRequest, "phoneNumber", "01087654321");

        given(authRepository.existsByEmail(testSignUpRequest.getEmail())).willReturn(true);

        assertThatThrownBy(() -> defaultSignUpService.signup(testSignUpRequest))
            .isInstanceOf(EmailOverlapException.class);
    }

    @Test
    @DisplayName("회원 이메일 중복체크 사용가능")
    void testExistsEmail() {
        given(authRepository.existsByEmail(any())).willReturn(false);
        given(mailUtils.sendMail(any())).willReturn(true);

        doNothing().when(redisUtils).set(any(), any());

        EmailRequest testEmailRequest = new EmailRequest();

        ReflectionTestUtils.setField(testEmailRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testEmailRequest, "isReferrer", false);

        defaultSignUpService.checkEmail(testEmailRequest);

        verify(authRepository, times(1)).existsByEmail(any());
    }

    @Test
    @DisplayName("회원 중복 이메일 예외처리")
    void testExistsEmailThrownByEmailOverlapException() {
        given(authRepository.existsByEmail(anyString())).willReturn(true);

        EmailRequest testEmailRequest = new EmailRequest();

        ReflectionTestUtils.setField(testEmailRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testEmailRequest, "isReferrer", false);


        assertThatThrownBy(() -> defaultSignUpService.checkEmail(testEmailRequest))
            .isInstanceOf(EmailOverlapException.class);

        verify(authRepository, times(1)).existsByEmail(any());
    }

    @Test
    @DisplayName("추천인 이메일이 있고, 존재하지 않는 이메일 경우 정상 처리")
    void testCheckEmail() {
        EmailRequest testEmailRequest = new EmailRequest();

        ReflectionTestUtils.setField(testEmailRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testEmailRequest, "isReferrer", true);

        given(authRepository.existsByEmail(anyString())).willReturn(true);

        defaultSignUpService.checkEmail(testEmailRequest);

        verify(authRepository, times(1)).existsByEmail(any());
    }

    @Test
    @DisplayName("회원 이메일 사용가능")
    void testUseEmailResponse() {
        EmailUseRequest emailUseRequest = new EmailUseRequest();
        ReflectionTestUtils.setField(emailUseRequest, "email", "overlap@email.com");

        given(redisUtils.hasKey(emailUseRequest.getEmail())).willReturn(true);
        doNothing().when(redisUtils).deleteAuth(anyString());

        defaultSignUpService.useEmail(emailUseRequest);

        verify(redisUtils, times(1)).deleteAuth(anyString());
    }

    @Test
    @DisplayName("사용하려는 이메일 중복 에러처리")
    void testUseEmailThrownByEmailOverlapException() {
        EmailUseRequest emailUseRequest = new EmailUseRequest();
        ReflectionTestUtils.setField(emailUseRequest, "email", "overlap@email.com");

        given(authRepository.existsByEmail(emailUseRequest.getEmail())).willReturn(true);

        assertThatThrownBy(() -> defaultSignUpService.useEmail(emailUseRequest))
            .isInstanceOf(EmailOverlapException.class);

    }

}

package com.nhnacademy.marketgg.auth.service.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.nhnacademy.marketgg.auth.config.WebSecurityConfig;
import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.dto.request.signup.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.signup.SignUpRequest;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.AuthRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.repository.auth.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.authrole.AuthRoleRepository;
import com.nhnacademy.marketgg.auth.repository.role.RoleRepository;
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
    @DisplayName("회원가입 테스트")
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
        given(authRepository.existsByEmail(any(String.class))).willReturn(true);

        EmailRequest testEmailRequest = new EmailRequest();

        ReflectionTestUtils.setField(testEmailRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testEmailRequest, "isReferrer", false);


        assertThatThrownBy(() -> defaultSignUpService.checkEmail(testEmailRequest))
                .isInstanceOf(EmailOverlapException.class);

        verify(authRepository, times(1)).existsByEmail(any());
    }

}

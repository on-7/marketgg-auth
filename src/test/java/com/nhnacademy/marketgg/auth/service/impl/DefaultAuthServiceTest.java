package com.nhnacademy.marketgg.auth.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.marketgg.auth.dto.request.SignupRequest;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.AuthRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.entity.Roles;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.AuthRoleRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
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

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

@SpringBootTest
class DefaultAuthServiceTest {

    @InjectMocks
    private DefaultAuthService authService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private AuthRoleRepository authRoleRepository;

    @Mock
    private RoleRepository roleRepository;

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
    void testSignup() throws RoleNotFoundException {

        SignupRequest testSignupRequest = new SignupRequest();

        ReflectionTestUtils.setField(testSignupRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignupRequest, "password", "1234");
        ReflectionTestUtils.setField(testSignupRequest, "name", "testName");

        Auth auth = new Auth(testSignupRequest);

        given(authRepository.save(any(Auth.class))).willReturn(auth);

        Long authNo = auth.getAuthNo();

        Role role = new Role();

        ReflectionTestUtils.setField(role, "roleNo", 0L);
        ReflectionTestUtils.setField(role, "name", Roles.ROLE_USER);

        given(roleRepository.findByName(Roles.ROLE_USER)).willReturn(Optional.of(role));

        AuthRole.Pk pk = new AuthRole.Pk(authNo, role.getRoleNo());

        AuthRole authRole = new AuthRole(pk, auth, role);

        given(authRoleRepository.save(any(AuthRole.class))).willReturn(authRole);

        authService.signup(testSignupRequest);

        verify(authRepository, times(1)).save(any(auth.getClass()));
        // getDeclaringClass() 메서드는 이 클래스의 선언 클래스를 가져오는 데 사용됨.
        verify(roleRepository, times(1)).findByName(any(Roles.ROLE_USER.getDeclaringClass()));
        verify(authRoleRepository, times(1)).save(any(authRole.getClass()));
    }

    @Test
    @DisplayName("회원 이메일 중복체크")
    void testExistsEmail() {

        given(authRepository.existsByEmail(any())).willReturn(true);

        authService.checkEmail("test@test.com");

        verify(authRepository, times(1)).existsByEmail(any());
    }

    @DisplayName("로그인 시 JWT 발급")
    @Test
    void testLogin() {
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "email", "email");
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
                   .put(loginRequest.getEmail(), "refresh_token", refreshToken);

        Assertions.assertThat(authService.login(loginRequest)).isEqualTo(jwt);
    }

}
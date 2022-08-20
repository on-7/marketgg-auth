package com.nhnacademy.marketgg.auth.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.jwt.CustomUser;
import com.nhnacademy.marketgg.auth.repository.auth.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.role.RoleRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

@SpringBootTest
class DefaultUserDetailsServiceTest {

    @InjectMocks
    private DefaultUserDetailsService defaultUserDetailsService;

    @Mock
    private AuthRepository authRepository;

    @Mock
    private RoleRepository roleRepository;

    @DisplayName("Email 로 회원 찾기")
    @Test
    void testLoadUserByUsername() {
        Auth mockAuth = mock(Auth.class);
        given(mockAuth.isMember()).willReturn(true);
        given(authRepository.findByEmail(anyString())).willReturn(Optional.of(mockAuth));
        given(roleRepository.findRolesByAuthId(anyLong())).willReturn(new ArrayList<>());

        UserDetails userDetails = defaultUserDetailsService.loadUserByUsername("email");

        then(authRepository).should(times(1)).findByEmail(anyString());
        then(roleRepository).should(times(1)).findRolesByAuthId(anyLong());

        assertThat(userDetails).isNotNull()
                               .isInstanceOf(CustomUser.class);
    }

    @DisplayName("회원 찾기 실패")
    @Test
    void testLoadUserByUsername_fail() {
        String email = "email";

        given(authRepository.findByEmail(email))
            .willThrow(new AuthNotFoundException(email));
        given(roleRepository.findRolesByAuthId(anyLong()))
            .willReturn(new ArrayList<>());

        assertThatThrownBy(() -> defaultUserDetailsService.loadUserByUsername(email))
            .isInstanceOf(AuthNotFoundException.class);
    }

}

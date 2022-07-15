package com.nhnacademy.marketgg.auth.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.jwt.CustomUser;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
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
        when(authRepository.findByEmail(anyString())).thenReturn(Optional.of(mock(Auth.class)));
        when(roleRepository.findRolesByAuthNo(anyLong())).thenReturn(new ArrayList<>());

        UserDetails userDetails = defaultUserDetailsService.loadUserByUsername("email");

        verify(authRepository, times(1)).findByEmail(anyString());
        verify(roleRepository, times(1)).findRolesByAuthNo(anyLong());

        assertThat(userDetails).isNotNull()
                               .isInstanceOf(CustomUser.class);
    }

    @DisplayName("회원 찾기 실패")
    @Test
    void testLoadUserByUsername_fail() {
        String email = "email";

        when(authRepository.findByEmail(email))
            .thenThrow(new AuthNotFoundException(email));
        when(roleRepository.findRolesByAuthNo(anyLong())).thenReturn(new ArrayList<>());

        assertThatThrownBy(() -> defaultUserDetailsService.loadUserByUsername(email))
            .isInstanceOf(AuthNotFoundException.class);
    }
}
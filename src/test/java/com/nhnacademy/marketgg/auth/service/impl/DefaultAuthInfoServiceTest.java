package com.nhnacademy.marketgg.auth.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
class DefaultAuthInfoServiceTest {

    @InjectMocks
    DefaultAuthInfoService authInfoService;

    @Mock
    AuthRepository authRepository;

    @Mock
    TokenUtils tokenUtils;

    @Mock
    RoleRepository roleRepository;

    @Mock
    RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("UUID 를 이용하여 사용자 찾기")
    void testFindAuthByUuid() {

        String uuid = UUID.randomUUID().toString();
        String token = "jwt";
        Auth auth = mock(Auth.class);
        given(auth.getEmail()).willReturn("email@gmail.com");
        given(auth.getName()).willReturn("홍길동");
        given(auth.getPhoneNumber()).willReturn("01012341234");

        given(tokenUtils.getUuidFromToken(token)).willReturn(uuid);
        given(authRepository.findByUuid(uuid)).willReturn(Optional.of(auth));

        MemberResponse memberResponse = authInfoService.findAuthByUuid(token);

        assertThat(memberResponse).isNotNull();
        assertThat(memberResponse.getEmail()).isEqualTo(auth.getEmail());

        then(tokenUtils).should(times(1)).getUuidFromToken(token);
        then(authRepository).should(times(1)).findByUuid(uuid);
    }

    @Test
    @DisplayName("UUID 로 사용자 찾기2")
    void testFindMemberInfoByUuid() {
        String uuid = UUID.randomUUID().toString();
        Auth auth = mock(Auth.class);

        given(auth.getName()).willReturn("홍길동");
        given(auth.getEmail()).willReturn("email@gmail.com");

        given(authRepository.findByUuid(uuid)).willReturn(Optional.of(auth));

        MemberInfoResponse memberInfoByUuid = authInfoService.findMemberInfoByUuid(uuid);

        assertThat(memberInfoByUuid.getEmail()).isEqualTo(auth.getEmail());
        assertThat(memberInfoByUuid.getName()).isEqualTo(auth.getName());

        then(authRepository).should(times(1)).findByUuid(uuid);
    }

}

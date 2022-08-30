package com.nhnacademy.marketgg.auth.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.nhnacademy.marketgg.auth.dto.response.AdminMemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.PageEntity;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.auth.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.role.RoleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Test
    @DisplayName("UUID 목록으로 회원 목록 조회")
    void findMemberNameList() {
        List<String> uuids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            uuids.add(UUID.randomUUID().toString());
        }

        List<MemberNameResponse> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new MemberNameResponse(uuids.get(i), "name" + (i + 1)));
        }

        given(authRepository.findMembersByUuid(anyList())).willReturn(list);

        List<MemberNameResponse> memberNameList = authInfoService.findMemberNameList(uuids);

        Assertions.assertThat(memberNameList).hasSize(5);
    }

    @Test
    @DisplayName("회원 목록 조회")
    void testFindAdminMembers() {
        PageImpl<AdminMemberResponse> adminMemberResponses = new PageImpl<>(new ArrayList<>());

        given(authRepository.findMembers(any(Pageable.class))).willReturn(adminMemberResponses);

        PageEntity<AdminMemberResponse> adminMembers = authInfoService.findAdminMembers(PageRequest.of(0, 10));

        assertThat(adminMembers).isNotNull();
    }

}

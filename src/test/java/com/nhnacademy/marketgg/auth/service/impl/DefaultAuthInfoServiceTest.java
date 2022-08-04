package com.nhnacademy.marketgg.auth.service.impl;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
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

}

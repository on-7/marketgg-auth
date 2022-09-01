package com.nhnacademy.marketgg.auth.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.dto.response.AdminMemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.common.PageEntity;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(AuthInfoController.class)
class AuthInfoControllerTest {

    MockMvc mockMvc;

    @Autowired
    AuthInfoController controller;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    AuthInfoService authInfoService;

    @MockBean
    AuthService authService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .alwaysDo(print())
                                 .build();
    }

    @Test
    @DisplayName("UUID 로 사용자 정보 요청1")
    void testGetAuthInfo() throws Exception {
        String email = "email@gmail.com";
        String name = "홍길동";
        String phoneNumber = "01012341234";
        MemberResponse memberResponse = new MemberResponse(email, name, phoneNumber);
        given(authInfoService.findAuthByUuid(any())).willReturn(memberResponse);

        mockMvc.perform(get("/members/info")
                   .header(HttpHeaders.AUTHORIZATION, "Bearer jwt"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success", equalTo(true)))
               .andExpect(jsonPath("$.data.email", equalTo(email)));
    }

    @Test
    @DisplayName("UUID 로 사용자 정보 요청2")
    void testGetMemberInfo() throws Exception {
        String uuid = UUID.randomUUID().toString();
        Map<String, String> memberInfoRequest = new HashMap<>();
        memberInfoRequest.put("uuid", uuid);
        String jsonMemberInfoRequest = mapper.writeValueAsString(memberInfoRequest);

        String email = "email@gmail.com";
        String name = "홍길동";
        String phoneNumber = "01012341234";
        MemberInfoResponse memberResponse = new MemberInfoResponse(name, email, phoneNumber);

        given(authInfoService.findMemberInfoByUuid(any())).willReturn(memberResponse);

        mockMvc.perform(post("/members/info/person")
                   .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(APPLICATION_JSON)
                   .content(jsonMemberInfoRequest))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success", equalTo(true)))
               .andExpect(jsonPath("$.data.email", equalTo(email)));
    }

    @Test
    @DisplayName("UUID 목록으로 회원 목록 조회")
    void getMemberList() throws Exception {
        List<String> uuids = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            uuids.add(UUID.randomUUID().toString());
        }

        List<MemberNameResponse> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new MemberNameResponse(uuids.get(i), "name" + (i + 1)));
        }

        String uuidList = mapper.writeValueAsString(uuids);

        given(authInfoService.findMemberNameList(anyList())).willReturn(list);

        mockMvc.perform(post("/members/info/names")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(uuidList))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.[0].uuid", equalTo(uuids.get(0))))
               .andExpect(jsonPath("$.data.[0].name", equalTo("name1")));
    }

    @Test
    @DisplayName("사용자 정보 목록 조회")
    void testRetrieveMembers() throws Exception {
        List<AdminMemberResponse> adminMemberResponses = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            adminMemberResponses.add(
                new AdminMemberResponse((long) i, UUID.randomUUID().toString(), "email" + i + "@mail.com", "Name" + i,
                    "0101234123" + i, LocalDateTime.now()));
        }

        PageEntity<AdminMemberResponse> entity = new PageEntity<>(1, 10, 3, adminMemberResponses);

        PageRequest pageRequest = PageRequest.of(1, 10);
        String request = mapper.writeValueAsString(pageRequest);
        String jwt = "Bearer jwt";

        given(authInfoService.findAdminMembers(any(PageRequest.class))).willReturn(entity);
        given(authInfoService.isAdmin(any())).willReturn(true);

        mockMvc.perform(get("/members/info/list?page=1")
                   .contentType(APPLICATION_JSON)
                   .header(HttpHeaders.AUTHORIZATION, jwt)
                   .content(request))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success", equalTo(true)))
               .andExpect(jsonPath("$.data.pageNumber", equalTo(pageRequest.getPageNumber())));
    }

}

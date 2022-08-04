package com.nhnacademy.marketgg.auth.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
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

        mockMvc.perform(get("/info")
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
        MemberInfoResponse memberResponse = new MemberInfoResponse(name, email);

        given(authInfoService.findMemberInfoByUuid(any())).willReturn(memberResponse);

        mockMvc.perform(get("/info/person")
                   .header(HttpHeaders.AUTHORIZATION, "Bearer jwt")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(APPLICATION_JSON)
                   .content(jsonMemberInfoRequest))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success", equalTo(true)))
               .andExpect(jsonPath("$.data.email", equalTo(email)));
    }

}

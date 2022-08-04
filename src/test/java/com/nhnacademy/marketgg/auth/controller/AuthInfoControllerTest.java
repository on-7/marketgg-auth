package com.nhnacademy.marketgg.auth.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

        mockMvc.perform(post("/info/names")
                   .characterEncoding(StandardCharsets.UTF_8)
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(uuidList))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.[0].uuid", equalTo(uuids.get(0))))
               .andExpect(jsonPath("$.data.[0].name", equalTo("name1")));
    }

}

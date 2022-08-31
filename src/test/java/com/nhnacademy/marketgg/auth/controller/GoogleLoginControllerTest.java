package com.nhnacademy.marketgg.auth.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.google.GoogleProfile;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.OauthLoginResponse;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.TokenResponse;
import com.nhnacademy.marketgg.auth.service.impl.GoogleLoginService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(GoogleLoginController.class)
class GoogleLoginControllerTest {

    MockMvc mockMvc;

    @Autowired
    GoogleLoginController controller;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    GoogleLoginService googleLoginService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .alwaysDo(print())
                                 .build();
    }

    @Test
    @DisplayName("구글 로그인")
    void testOauthLogin() throws Exception {
        String token = "jwt";
        LocalDateTime now = LocalDateTime.now();
        TokenResponse tokenResponse = new TokenResponse(token, now);
        OauthLoginResponse oauthLoginResponse = OauthLoginResponse.loginSuccess(tokenResponse);
        Map<String, String> request = Map.of("code", "code");

        String jsonRequest = mapper.writeValueAsString(request);

        given(googleLoginService.requestProfile(request.get("code"))).willReturn(oauthLoginResponse);

        mockMvc.perform(post("/members/login/google")
                   .contentType(APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .content(jsonRequest))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success", equalTo(true)))
               .andExpect(header().exists(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @DisplayName("구글 로그인 실패")
    void testOauthLoginFail() throws Exception {
        String email = "email@gmail.com";
        String name = "홍길동";
        GoogleProfile googleProfile = new GoogleProfile();
        ReflectionTestUtils.setField(googleProfile, "email", email);
        ReflectionTestUtils.setField(googleProfile, "name", name);

        OauthLoginResponse loginResponse = OauthLoginResponse.doSignUp(googleProfile);
        Map<String, String> request = Map.of("code", "code");

        String jsonRequest = mapper.writeValueAsString(request);

        given(googleLoginService.requestProfile(request.get("code"))).willReturn(loginResponse);

        mockMvc.perform(post("/members/login/google")
                   .contentType(APPLICATION_JSON)
                   .characterEncoding(StandardCharsets.UTF_8)
                   .content(jsonRequest))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.success", equalTo(true)))
               .andExpect(jsonPath("$.data.name", equalTo(name)))
               .andExpect(jsonPath("$.data.email", equalTo(email)));
    }

}

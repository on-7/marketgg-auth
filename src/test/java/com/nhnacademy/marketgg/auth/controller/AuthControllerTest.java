package com.nhnacademy.marketgg.auth.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.config.WebSecurityConfig;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
@MockBean({
    AuthenticationManager.class,
    TokenUtils.class,
    RedisTemplate.class
})
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    AuthService authService;

    @MockBean
    UserDetailsService userDetailsService;

    @Test
    @DisplayName("JWT 갱신 요청")
    void testRenewToken() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        TokenResponse tokenResponse = new TokenResponse("jwt", now);

        given(authService.renewToken("JWT-TOKEN")).willReturn(tokenResponse);

        mockMvc.perform(get("/auth/refresh")
                   .header(HttpHeaders.AUTHORIZATION, "Bearer JWT-TOKEN"))
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer jwt"))
               .andDo(print());
    }

    @Test
    @DisplayName("JWT 갱신 요청 시 리프레시 토큰 만료")
    void testRenewTokenFail() throws Exception {
        given(authService.renewToken("JWT-TOKEN")).willReturn(null);

        mockMvc.perform(get("/auth/refresh")
                   .header(HttpHeaders.AUTHORIZATION, "Bearer JWT-TOKEN"))
               .andExpect(status().isUnauthorized())
               .andDo(print());
    }

    @Test
    @DisplayName("로그아웃")
    void testLogout() throws Exception {
        mockMvc.perform(get("/auth/logout")
                   .header(HttpHeaders.AUTHORIZATION, "Bearer JWT-TOKEN"))
               .andDo(print());
        doNothing().when(authService).logout("JWT-TOKEN");

    }

}

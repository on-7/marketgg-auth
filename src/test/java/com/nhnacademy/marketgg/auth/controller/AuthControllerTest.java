package com.nhnacademy.marketgg.auth.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.aspect.TokenAspect;
import com.nhnacademy.marketgg.auth.config.WebSecurityConfig;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.AuthService;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(AuthController.class)
@Import({ WebSecurityConfig.class, TokenAspect.class })
@MockBean({
    AuthenticationManager.class,
    TokenUtils.class,
    RedisTemplate.class
})
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TokenUtils tokenUtils;

    @MockBean
    AuthService authService;

    @MockBean
    UserDetailsService userDetailsService;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                                 .alwaysDo(print())
                                 .build();
    }

    @Test
    @DisplayName("JWT 갱신 요청")
    void testRenewToken() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        Authentication authentication = new UsernamePasswordAuthenticationToken("username", "password");
        String token = createToken(authentication, new Date(), 60_000L);
        TokenResponse tokenResponse = new TokenResponse(token, now);

        given(authService.renewToken(token)).willReturn(tokenResponse);

        mockMvc.perform(get("/refresh")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andExpect(status().isOk())
               .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer " + token));
    }

    @Test
    @DisplayName("JWT 갱신 요청 시 리프레시 토큰 만료")
    void testRenewTokenFail() throws Exception {
        given(authService.renewToken("JWT-TOKEN")).willReturn(null);

        mockMvc.perform(get("/refresh")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer JWT-TOKEN"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃")
    void testLogout() throws Exception {
        String token = "JWT-TOKEN";
        doNothing().when(authService).logout(token);

        mockMvc.perform(get("/logout")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer JWT-TOKEN"));

        verify(authService, times(1)).logout(token);
    }

    private String createToken(Authentication authentication, Date issueDate, long expirationDate) {
        ReflectionTestUtils.setField(tokenUtils, "key", Keys.hmacShaKeyFor(
            Decoders.BASE64URL.decode("test-keytest-keytest-keytest-keytest-keytest-key")));

        return ReflectionTestUtils.invokeMethod(tokenUtils, "createToken", authentication, issueDate, expirationDate);
    }

}

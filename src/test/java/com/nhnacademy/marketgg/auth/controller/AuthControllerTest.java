package com.nhnacademy.marketgg.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.config.WebSecurityConfig;
import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.ExistEmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.SignUpResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
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

    @MockBean
    AuthService authService;

    @MockBean
    UserDetailsService userDetailsService;

    @MockBean
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 테스트")
    void testDoSignup() throws Exception {

        SignUpRequest testSignUpRequest = new SignUpRequest();

        ReflectionTestUtils.setField(testSignUpRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignUpRequest, "password", "1234");
        ReflectionTestUtils.setField(testSignUpRequest, "name", "testName");
        ReflectionTestUtils.setField(testSignUpRequest, "phoneNumber", "010-1234-1234");

        when(authService.signup(testSignUpRequest)).thenReturn(any(SignUpResponse.class));

        mockMvc.perform(post("/auth/signup")
                   .contentType(APPLICATION_JSON)
                   .content(mapper.writeValueAsString(testSignUpRequest)))
               .andExpect(status().isCreated())
               .andDo(print());
    }

    @Test
    @DisplayName("회원 이메일 중복체크 사용가능")
    void testCheckEmail() throws Exception {
        EmailRequest testEmailRequest = new EmailRequest();

        ReflectionTestUtils.setField(testEmailRequest, "email", "testEmail");
        ReflectionTestUtils.setField(testEmailRequest, "isReferrer", true);

        when(authService.checkEmail(testEmailRequest))
            .thenReturn(any(ExistEmailResponse.class));

        mockMvc.perform(post("/auth/check/email")
                   .contentType(APPLICATION_JSON)
                   .content(mapper.writeValueAsString(testEmailRequest)))
               .andExpect(status().isOk())
               .andDo(print());
    }

    // TODO : 테스트 코드 로직수정 필요
    // @Test
    // @DisplayName("회원 이메일 중복체크 예외처리")
    // void testExistsEmailThrownByEmailOverlapException() throws Exception {
    //     EmailRequest emailRequest = new EmailRequest();
    //
    //     ReflectionTestUtils.setField(emailRequest, "email", "testEmail");
    //     ReflectionTestUtils.setField(emailRequest, "isReferrer", false);
    //
    //     when(authService.checkEmail(emailRequest))
    //         .thenThrow(new EmailOverlapException(emailRequest.getEmail()));
    //
    //     mockMvc.perform(post("/auth/check/email")
    //                .contentType(APPLICATION_JSON)
    //                .content(mapper.writeValueAsString(emailRequest)))
    //            .andExpect(result -> assertTrue(
    //                result.getResolvedException() instanceof EmailOverlapException));
    //
    // }

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

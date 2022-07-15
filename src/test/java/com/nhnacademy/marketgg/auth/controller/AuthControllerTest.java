package com.nhnacademy.marketgg.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.config.WebSecurityConfig;
import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignupRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
@MockBean(
    AuthenticationManager.class
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AuthService authService;


    @Test
    @DisplayName("회원가입 테스트")
    void testDoSignup() throws Exception {
        SignupRequest testSignupRequest = new SignupRequest();

        ReflectionTestUtils.setField(testSignupRequest, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignupRequest, "password", "1234");
        ReflectionTestUtils.setField(testSignupRequest, "name", "testName");

        doNothing().when(authService).signup(testSignupRequest);

        mockMvc.perform(post("/auth/signup")
                   .contentType(APPLICATION_JSON)
                   .content(mapper.writeValueAsString(testSignupRequest)))
               .andExpect(status().isCreated())
               .andDo(print());

    }

    @Test
    @DisplayName("회원 이메일 중복 테스트")
    void testCheckEmail() throws Exception {
        EmailRequest emailRequest = new EmailRequest();

        ReflectionTestUtils.setField(emailRequest, "email", "testEmail");

        when(authService.checkEmail(emailRequest.getEmail())).thenReturn(any(EmailResponse.class));

        mockMvc.perform(post("/auth/check/email")
                   .contentType(APPLICATION_JSON)
                   .content(mapper.writeValueAsString(emailRequest)))
               .andExpect(status().isOk())
               .andDo(print());
    }

    @DisplayName("로그인 시 헤더에 jwt 토큰 저장")
    @Test
    void testDoLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "email", "email");
        ReflectionTestUtils.setField(loginRequest, "password", "password");

        String jsonLoginRequest = mapper.writeValueAsString(loginRequest);

        when(authService.login(any(loginRequest.getClass()))).thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                   .contentType(APPLICATION_JSON)
                   .content(jsonLoginRequest))
               .andExpect(status().isOk())
               .andExpect(header().exists("Authorization"))
               .andExpect(header().string("Authorization", "Bearer jwt-token"))
               .andDo(print());
    }

}

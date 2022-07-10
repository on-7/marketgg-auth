package com.nhnacademy.marketgg.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.config.WebSecurityConfig;
import com.nhnacademy.marketgg.auth.dto.EmailRequestDto;
import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.dto.UsernameRequestDto;
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

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
@MockBean(
        {AuthenticationManager.class}
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입 테스트")
    void testDoSignup() throws Exception {

        SignupRequestDto testSignupRequestDto = new SignupRequestDto();

        ReflectionTestUtils.setField(testSignupRequestDto, "username", "testUsername");
        ReflectionTestUtils.setField(testSignupRequestDto, "password", "1234");
        ReflectionTestUtils.setField(testSignupRequestDto, "email", "test@test.com");
        ReflectionTestUtils.setField(testSignupRequestDto, "name", "testName");

        doNothing().when(authService).signup(testSignupRequestDto);

        mockMvc.perform(post("/auth/signup")
                       .contentType(APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(testSignupRequestDto)))
               .andExpect(status().isCreated())
               .andDo(print());

    }

    @Test
    @DisplayName("회원 아이디 중복 테스트")
    void testExistsUsername() throws Exception {

        UsernameRequestDto usernameRequestDto = new UsernameRequestDto();

        ReflectionTestUtils.setField(usernameRequestDto, "username", "testUsername");

        doReturn(true).when(authService).existsUsername(usernameRequestDto.getUsername());

        mockMvc.perform(post("/auth/find/username")
                       .contentType(APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(usernameRequestDto)))
               .andExpect(status().isOk())
               .andDo(print());

    }

    @Test
    @DisplayName("회원 이메일 중복 테스트")
    void testExistsEmail() throws Exception {

        EmailRequestDto emailRequestDto = new EmailRequestDto();

        ReflectionTestUtils.setField(emailRequestDto, "email", "testUsername");

        when(authService.existsEmail(emailRequestDto.getEmail())).thenReturn(true);

        mockMvc.perform(post("/auth/find/email")
                       .contentType(APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(emailRequestDto)))
               .andExpect(status().isOk())
               .andDo(print());
    }
}
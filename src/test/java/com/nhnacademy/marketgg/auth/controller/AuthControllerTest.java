package com.nhnacademy.marketgg.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
}
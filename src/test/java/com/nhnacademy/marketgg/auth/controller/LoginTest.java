package com.nhnacademy.marketgg.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;
import com.nhnacademy.marketgg.auth.service.SignUpService;
import java.util.List;
import javax.management.relation.RoleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles({
    "local"
})
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class LoginTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthService authService;

    @Autowired
    SignUpService signUpService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ObjectMapper mapper;

    @BeforeEach
    void setUp() throws RoleNotFoundException {
        Role roleAdmin = new Role(Roles.ROLE_ADMIN);
        ReflectionTestUtils.setField(roleAdmin, "id", 0L);
        Role roleUser = new Role(Roles.ROLE_USER);
        ReflectionTestUtils.setField(roleUser, "id", 1L);

        roleRepository.saveAll(List.of(roleUser, roleAdmin));

        SignUpRequest signUpRequest = getSignupRequest();
        signUpService.signup(signUpRequest);
    }

    @Transactional
    @Test
    @DisplayName("로그인")
    void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "email", "eamil@email.com");
        ReflectionTestUtils.setField(loginRequest, "password", "password");

        String requestJson = mapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/members/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
               .andExpect(status().isOk())
               .andExpect(header().exists(HttpHeaders.AUTHORIZATION));
    }

    @Transactional
    @Test
    @DisplayName("로그인실패")
    void testLoginFail() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        ReflectionTestUtils.setField(loginRequest, "email", "eamil@email.com");
        ReflectionTestUtils.setField(loginRequest, "password", "wrong-password");

        String requestJson = mapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/members/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
               .andExpect(status().isUnauthorized())
               .andDo(print());
    }

    private SignUpRequest getSignupRequest() {
        SignUpRequest signUpRequest = new SignUpRequest();
        ReflectionTestUtils.setField(signUpRequest, "email", "eamil@email.com");
        ReflectionTestUtils.setField(signUpRequest, "password", "password");
        ReflectionTestUtils.setField(signUpRequest, "name", "홍길동");
        ReflectionTestUtils.setField(signUpRequest, "phoneNumber", "01012341234");
        return signUpRequest;
    }

}

package com.nhnacademy.marketgg.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.nhnacademy.marketgg.auth.controller.AuthController;
import com.nhnacademy.marketgg.auth.controller.AuthInfoController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class AuthApplicationTests {

    @Test
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
    }

}

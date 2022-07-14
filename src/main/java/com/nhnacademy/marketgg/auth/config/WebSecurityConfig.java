package com.nhnacademy.marketgg.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    /**
     * @param configuration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Blowfish 알고리즘을 기반으로 비밀번호를 암호화합니다.
     *
     * @return 암호화 가능한 단방향 해시 함수인 BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증을 처리하는 여러 개의 SecurityFilter 를 담는 filter chain 입니다.
     *
     * @param http - 세부 보안 기능을 설정할 수 있는 API 제공 클래스
     * @return 인증 처리와 관련된 SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().disable()
            .httpBasic().disable()
            .formLogin().disable();

        http.authorizeRequests()
            .antMatchers("/auth/**").permitAll();

        http.headers()
            .frameOptions().sameOrigin();

        return http.build();
    }

    /**
     * WebSecurity 커스터마이징을 지원합니다.
     *
     * @return WebSecurity 커스터마이징이 적용된 WebSecurityCustomizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                         .antMatchers("/h2-console/**");
    }

}

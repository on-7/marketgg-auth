package com.nhnacademy.marketgg.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 기본 설정을 진행합니다.
 *
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final ObjectMapper mapper;
    private final TokenGenerator tokenGenerator;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 인증을 관리하는 AuthenticationManger 를 반환합니다.
     *
     * @param configuration - 인증 구성을 내보냅니다.
     * @return 인증 정보를 관리하는 AuthenticationManager 를 관리한다.
     * @throws Exception getAuthenticationManager() 에서 throw 하는 예외입니다.
     * @see <a href="https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/authentication/configuration/AuthenticationConfiguration.html">AuthenticationConfiguration</a>
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
     * @throws Exception Spring Security 의 메소드에서 발생하는 예외입니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .addFilter(getJwtAuthenticationFilter());

        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.httpBasic().disable()
            .formLogin().disable();

        http.authorizeRequests()
            .antMatchers("/auth/**").permitAll();

        http.headers()
            .frameOptions().sameOrigin();

        return http.build();
    }

    private JwtAuthenticationFilter getJwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(authenticationManager(null),
                                            mapper, tokenGenerator, redisTemplate);

        jwtAuthenticationFilter.setFilterProcessesUrl("/auth/login");

        return jwtAuthenticationFilter;
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

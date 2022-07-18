package com.nhnacademy.marketgg.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.exception.InvalidLoginRequestException;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.server.MethodNotAllowedException;

/**
 * 사용자의 인증 요청 시 작동하는 Filter 입니다.
 * /auth/login 을 통한 요청을 처리합니다.
 *
 * @version 1.0.0
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private final ObjectMapper mapper;
    private final TokenGenerator tokenGenerator;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 생성자입니다.
     *
     * @param authenticationManager - Spring Security 에서 제공하는 권한을 관리하는 객체입니다.
     * @param mapper - JSON 을 매핑하고 파싱합니다.
     * @param tokenGenerator - JWT 생성 및 검증 관련 로직을 수행합니다.
     * @param redisTemplate - Redis 에 접근하기 위해 사용됩니다.
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper mapper,
                                   TokenGenerator tokenGenerator,
                                   RedisTemplate<String, Object> redisTemplate) {

        super(authenticationManager);
        this.mapper = mapper;
        this.tokenGenerator = tokenGenerator;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
        throws AuthenticationException {

        if (Objects.equals(request.getMethod(), HttpMethod.GET.name())) {
            throw new MethodNotAllowedException(HttpMethod.GET, List.of(HttpMethod.POST));
        }

        try {
            LoginRequest loginRequest =
                mapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken token
                = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                    loginRequest.getPassword());

            return getAuthenticationManager().authenticate(token);

        } catch (IOException e) {
            log.error("잘못된 로그인 요청");
            throw new InvalidLoginRequestException("잘못된 로그인 요청입니다");
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
        throws IOException, ServletException {

        Date issueDate = new Date();
        String jwt = tokenGenerator.generateJwt(authResult, issueDate);

        redisTemplate.opsForHash().put(authResult.getName(), REFRESH_TOKEN,
            tokenGenerator.generateRefreshToken(authResult, issueDate));

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
        throws IOException, ServletException {

        log.error("로그인 실패", failed);

        throw new LoginFailException();
    }
}

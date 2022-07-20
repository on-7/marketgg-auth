package com.nhnacademy.marketgg.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.exception.InvalidLoginRequestException;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 로그인 시 실행되는 필터입니다.
 *
 * @version 1.0.0
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper mapper;
    private final TokenUtils tokenUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper mapper,
                                   TokenUtils tokenUtils,
                                   RedisTemplate<String, Object> redisTemplate) {

        super(authenticationManager);
        this.mapper = mapper;
        this.tokenUtils = tokenUtils;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
        throws AuthenticationException {
        try {
            log.info("start login");

            LoginRequest loginRequest =
                mapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
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
        String jwt = tokenUtils.generateJwt(authResult, issueDate);
        String refreshToken = tokenUtils.generateRefreshToken(authResult, issueDate);

        redisTemplate.opsForHash().put(authResult.getName(), TokenUtils.REFRESH_TOKEN,
            refreshToken);

        redisTemplate.expireAt(authResult.getName(),
            new Date(issueDate.getTime() + tokenUtils.getRefreshTokenExpirationDate()));

        Date tokenExpireDate =
            new Date(issueDate.getTime() + tokenUtils.getTokenExpirationDate());
        LocalDateTime ldt = tokenExpireDate.toInstant()
                                           .atZone(ZoneId.systemDefault())
                                           .toLocalDateTime()
                                           .withNano(0);

        response.addHeader(HttpHeaders.AUTHORIZATION, TokenUtils.BEARER + jwt);
        response.addHeader(TokenUtils.JWT_EXPIRE, ldt.toString());
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

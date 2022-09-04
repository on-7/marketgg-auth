package com.nhnacademy.marketgg.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.response.login.oauth.TokenResponse;
import com.nhnacademy.marketgg.auth.exception.InvalidLoginRequestException;
import com.nhnacademy.marketgg.auth.jwt.CustomUser;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import java.io.IOException;
import java.util.Objects;
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
 * @author 윤동열
 * @version 1.0.0
 */
@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    private static final String WITHDRAW = "WITHDRAW";

    private final ObjectMapper mapper;
    private final TokenUtils tokenUtils;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * JWT 인증 필터를 위한 생성자입니다.
     *
     * @param authenticationManager - 인증 매니저
     * @param mapper                - (역)직렬화를 위한 매퍼
     * @param tokenUtils            - 토큰과 관련된 유틸리티 객체
     * @param redisTemplate         - Redis 데이터베이스 사용을 위한 템플릿
     */
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, ObjectMapper mapper,
                                   TokenUtils tokenUtils, RedisTemplate<String, Object> redisTemplate) {

        super(authenticationManager);
        this.mapper = mapper;
        this.tokenUtils = tokenUtils;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        try {
            log.info("start login");
            log.info("uri = {}", request.getRequestURI());

            LoginRequest loginRequest = mapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

            Authentication authentication = getAuthenticationManager().authenticate(token);
            CustomUser principal = (CustomUser) authentication.getPrincipal();
            if (principal.isWithdraw()) {
                response.setHeader(WWW_AUTHENTICATE, WITHDRAW);
            }

            return authentication;
        } catch (IOException e) {
            log.error("잘못된 로그인 요청");
            throw new InvalidLoginRequestException("잘못된 로그인 요청입니다");
        }
    }

    /**
     * 로그인이 성공했을 때 실행하는 메서드입니다. 레디스에 Refresh token 을 적절히 넣어줍니다.
     *
     * @param request    - HTTP 서블릿 요청 객체
     * @param response   - HTTP 서블릿 응답 객체
     * @param chain      - 필터 체인
     * @param authResult the object returned from the <tt>attemptAuthentication</tt>
     *                   method.
     * @throws IOException      - 입출력 예외 발생
     * @throws ServletException - 서블릿 관련 예외 발생
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
        throws IOException, ServletException {
        String withdrawHeader = response.getHeader(WWW_AUTHENTICATE);
        if (Objects.nonNull(withdrawHeader) && Objects.equals(withdrawHeader, WITHDRAW)) {
            return;
        }

        TokenResponse tokenResponse = tokenUtils.saveRefreshToken(redisTemplate, authResult);

        response.addHeader(HttpHeaders.AUTHORIZATION, TokenUtils.BEARER + tokenResponse.getJwt());
        response.addHeader(TokenUtils.JWT_EXPIRE, tokenResponse.getExpiredDate().toString());
    }

    /**
     * 로그인이 실패했을 때 실행하는 메서드입니다.
     *
     * @param request  - HTTP 서블릿 요청 객체
     * @param response - HTTP 서블릿 응답 객체
     * @param failed   - 로그인 실패 시 인증 예외 발생
     * @throws IOException      - 입출력 예외 발생
     * @throws ServletException - 서블릿 관련 예외 발생
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        log.error("로그인 실패: {}", failed.toString());
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }

}

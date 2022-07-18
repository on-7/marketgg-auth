package com.nhnacademy.marketgg.auth.jwt;

import static java.util.stream.Collectors.toList;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * JWT 토큰을 생성하고 필요한 정보를 제공하는 클래스입니다.
 */
@Slf4j
@Component
public class TokenGenerator {

    private static final String AUTHORITIES = "AUTHORITIES";

    private final RestTemplate restTemplate;
    private final Key key;

    @Getter
    private final long tokenExpirationDate;

    @Getter
    private final long refreshTokenExpirationDate;

    /**
     * 생성자입니다.
     *
     * @param secretUrl                  - JWT Secret 키를 요청하는 URL
     * @param tokenExpirationDate        - JWT 의 유효기간
     * @param refreshTokenExpirationDate - Refresh Token 의 유효기간
     * @param restTemplate               - restTemplate 스프링 빈을 주입받습니다.
     */
    public TokenGenerator(RestTemplate restTemplate,
                          @Value("${jwt.secret-url}") String secretUrl,
                          @Value("${jwt.expire-time}") long tokenExpirationDate,
                          @Value("${jwt.refresh-expire-time}") long refreshTokenExpirationDate) {
        this.restTemplate = restTemplate;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(getJwtSecret(secretUrl)));
        this.tokenExpirationDate = tokenExpirationDate;
        this.refreshTokenExpirationDate = refreshTokenExpirationDate;
    }

    /**
     * JWT 를 생성합니다.
     *
     * @param authentication - 사용자 정보
     * @param issueDate      - 토큰 발행일
     * @return 생성된 JWT
     */
    public String generateJwt(Authentication authentication, Date issueDate) {
        return createToken(authentication, issueDate, tokenExpirationDate);
    }

    /**
     * Refresh Token 을 생성합니다.
     *
     * @param authentication - 사용자 정보
     * @param issueDate      - 토큰 발행일자
     * @return 생성된 Refresh 토큰
     */
    public String generateRefreshToken(Authentication authentication, Date issueDate) {
        return createToken(authentication, issueDate, refreshTokenExpirationDate);
    }

    /**
     * 토큰을 생성합니다.
     *
     * @param authentication - 사용자 정보
     * @param issueDate      - 토큰 발행일자
     * @param expirationDate - 토큰 만료일자
     * @return JWT
     */
    private String createToken(Authentication authentication, Date issueDate, long expirationDate) {

        return Jwts.builder()
                   .setSubject(authentication.getName())
                   .claim(AUTHORITIES,
                       authentication.getAuthorities()
                                     .stream()
                                     .map(GrantedAuthority::getAuthority)
                                     .collect(toList()))
                   .setIssuedAt(issueDate)
                   .setExpiration(new Date(issueDate.getTime() + expirationDate))
                   .signWith(key)
                   .compact();
    }

    /**
     * 토큰을 이용하여 사용자의 Email 정보를 얻습니다.
     *
     * @param token JWT
     * @return 사용자의 이메일
     */
    public String getUuid(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {

        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    /**
     * 토큰의 유효성을 체크합니다.
     *
     * @param token JWT
     * @return 유효성 검사 결과를 반환합니다.
     */
    public boolean isInvalidToken(String token) {
        try {
            getClaims(token);

            return false;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("{}, {}", e, token);
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return true;
    }

    public long getExpireDate(String token) {
        return getClaims(token).getExpiration().getTime();
    }

    /**
     * 만료된 토큰에서 사용자의 Email 정보를 얻습니다.
     *
     * @param token - 만료된 JWT
     * @return 사용자의 UUID 를 반환받는다.
     */
    public String getUuidFromExpiredToken(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    /**
     * 만료된 JWT 를 파싱하여 Authentication 객체를 얻습니다.
     *
     * @param jwt  - JWT
     * @param uuid - 사용자 uuid
     * @return Authentication 객체
     */
    public Authentication getAuthenticationFromExpiredToken(String jwt, String uuid) {
        Collection<String> roles;

        try {
            roles = getClaims(jwt).get(AUTHORITIES, Collection.class);
        } catch (ExpiredJwtException e) {
            roles = e.getClaims().get(AUTHORITIES, Collection.class);
        }

        Collection<GrantedAuthority> authorities = roles.stream()
                                                        .map(SimpleGrantedAuthority::new)
                                                        .collect(toList());

        return new UsernamePasswordAuthenticationToken(uuid, "", authorities);
    }

    private String getJwtSecret(String jwtSecretUrl) {
        Map<String, Map<String, String>> response =
            restTemplate.getForObject(jwtSecretUrl, Map.class);

        return Optional.ofNullable(response)
                       .orElseThrow(IllegalArgumentException::new)
                       .get("body")
                       .get("secret");
    }

}

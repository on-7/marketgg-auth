package com.nhnacademy.marketgg.auth.jwt;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰을 생성하고 필요한 정보를 제공하는 클래스입니다.
 */

@Slf4j
@Component
public class TokenGenerator {

    private final Key key;
    private final long tokenExpirationDate;
    private final long refreshTokenExpirationDate;
    private final String authKey;

    /**
     * 생성자입니다.
     *
     * @param secret - JWT Secret
     * @param tokenExpirationDate - JWT 의 유효기간
     * @param refreshTokenExpirationDate - Refresh Token 의 유효기간
     * @param authKey - JWT 에 권한 정보를 담을 때 쓰는 키
     */
    public TokenGenerator(@Value("${jwt.secret}") String secret,
                          @Value("${jwt.expire-time}") long tokenExpirationDate,
                          @Value("${jwt.refresh-expire-time}") long refreshTokenExpirationDate,
                          @Value("${jwt.auth-key}") String authKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
        this.tokenExpirationDate = tokenExpirationDate;
        this.refreshTokenExpirationDate = refreshTokenExpirationDate;
        this.authKey = authKey;
    }

    /**
     * JWT 를 생성합니다.
     *
     * @param authentication 사용자 정보
     * @param issueDate      토큰 발행일
     * @return 생성된 JWT
     */
    public String generateJwt(Authentication authentication, Date issueDate) {
        return createToken(authentication, issueDate, tokenExpirationDate);
    }

    /**
     * Refresh Token 을 생성합니다.
     *
     * @param authentication 사용자 정보
     * @param issueDate      토큰 발행일자
     * @return               생성된 Refresh 토큰
     */
    public String generateRefreshToken(Authentication authentication, Date issueDate) {
        return createToken(authentication, issueDate, refreshTokenExpirationDate);
    }

    /**
     * 토큰을 생성합니다.
     *
     * @param authentication 사용자 정보
     * @param issueDate      토큰 발행일자
     * @param expirationDate 토큰 만료일자
     * @return               JWT
     */
    private String createToken(Authentication authentication, Date issueDate, long expirationDate) {
        return Jwts.builder()
                   .setSubject(authentication.getName())
                   .claim(authKey, authentication.getAuthorities())
                   .setIssuedAt(issueDate)
                   .setExpiration(new Date(System.currentTimeMillis() + expirationDate))
                   .signWith(key)
                   .compact();
    }

    /**
     * 토큰을 이용하여 사용자의 Email 정보를 얻습니다.
     *
     * @param token JWT
     * @return 사용자의 이메일
     */
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * 토큰에 저장된 클레임 전체 정보를 얻습니다.
     *
     * @param token JWT
     * @return JWT 에 저장된 클레임
     */
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
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);

            return false;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * JWT 를 파싱하여 Authentication 객체를 얻습니다.
     *
     * @param jwt    JWT
     * @param email  사용자 Email
     * @return       Authentication 객체
     */
    public Authentication getAuthentication(String jwt, String email) {
        Collection<? extends GrantedAuthority> roles =
            getClaims(jwt).get(authKey, Collection.class);
        return new UsernamePasswordAuthenticationToken(email, "", roles);
    }

}

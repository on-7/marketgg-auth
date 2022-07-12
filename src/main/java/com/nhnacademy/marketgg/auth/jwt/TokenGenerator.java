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

@Slf4j
@Component
public class TokenGenerator {

    private final Key key;
    private final long tokenExpirationDate;
    private final long refreshTokenExpirationDate;
    private final String authKey;

    public TokenGenerator(@Value("${jwt.secret}") String secret,
                          @Value("${jwt.expire-time}") long tokenExpirationDate,
                          @Value("${jwt.refresh-expire-time}") long refreshTokenExpirationDate,
                          @Value("${jwt.auth-key}") String authKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
        this.tokenExpirationDate = tokenExpirationDate;
        this.refreshTokenExpirationDate = refreshTokenExpirationDate;
        this.authKey = authKey;
    }

    public String generateJwt(Authentication authentication, Date issueDate) {
        return createToken(authentication, issueDate, tokenExpirationDate);
    }

    public String generateRefreshToken(Authentication authentication, Date issueDate) {
        return createToken(authentication, issueDate, refreshTokenExpirationDate);
    }

    private String createToken(Authentication authentication, Date issueDate, long expirationDate) {
        return Jwts.builder()
                   .setSubject(authentication.getName())
                   .claim(authKey, authentication.getAuthorities())
                   .setIssuedAt(issueDate)
                   .setExpiration(new Date(System.currentTimeMillis() + expirationDate))
                   .signWith(key)
                   .compact();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

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

    public Authentication getAuthentication(String jwt, String username) {
        Collection<? extends GrantedAuthority> roles = getClaims(jwt).get(authKey, Collection.class);
        return new UsernamePasswordAuthenticationToken(username, "", roles);
    }

}

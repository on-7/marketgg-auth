package com.nhnacademy.marketgg.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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

    public String generateJwt(Authentication authentication) {
        return createToken(authentication, tokenExpirationDate);
    }

    public String generateRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenExpirationDate);
    }

    private String createToken(Authentication authentication, long expirationDate) {
        return Jwts.builder()
                   .setSubject(authentication.getName())
                   .claim(authKey, authentication.getAuthorities())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expirationDate))
                   .signWith(key)
                   .compact();
    }

}

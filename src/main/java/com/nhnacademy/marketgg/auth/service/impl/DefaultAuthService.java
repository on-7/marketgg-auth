package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenUtils tokenUtils;

    @Override
    public void logout(final String token) {
        String uuid = tokenUtils.getUuidFromToken(token);

        redisTemplate.opsForHash().delete(uuid, TokenUtils.REFRESH_TOKEN);

        long tokenExpireTime = tokenUtils.getExpireDate(token) - System.currentTimeMillis();
        redisTemplate.opsForValue().set(token, true, tokenExpireTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public TokenResponse renewToken(final String token) {
        String uuid = tokenUtils.getUuidFromToken(token);

        String refreshToken =
            (String) redisTemplate.opsForHash().get(uuid, TokenUtils.REFRESH_TOKEN);

        if (this.isInvalidToken(uuid, refreshToken)) {
            return null;
        }

        redisTemplate.opsForHash().delete(uuid, TokenUtils.REFRESH_TOKEN);

        Authentication authentication =
            tokenUtils.getAuthenticationFromExpiredToken(token, uuid);

        return tokenUtils.saveRefreshToken(redisTemplate, authentication);
    }

    private boolean isInvalidToken(String uuid, String refreshToken) {
        return Objects.isNull(refreshToken)
            || tokenUtils.isInvalidToken(refreshToken)
            || !Objects.equals(uuid, tokenUtils.getUuidFromToken(refreshToken));
    }

}

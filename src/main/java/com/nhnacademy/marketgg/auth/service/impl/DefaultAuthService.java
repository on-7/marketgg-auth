package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.response.login.oauth.TokenResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.auth.AuthRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 비즈니스 로직을 처리하는 기본 구현체입니다.
 * <p>
 * {@inheritDoc}
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private final AuthRepository authRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenUtils tokenUtils;

    @Override
    public void logout(final String token) {
        String uuid = tokenUtils.getUuidFromToken(token);

        redisTemplate.opsForHash().delete(uuid, TokenUtils.REFRESH_TOKEN);

        tokenUtils.setBlackList(redisTemplate, token);
    }

    @Override
    public TokenResponse renewToken(final String token) {
        String uuid = tokenUtils.getUuidFromToken(token);

        Optional<Auth> auth = authRepository.findByUuid(uuid).filter(Auth::isMember);

        String refreshToken =
                (String) redisTemplate.opsForHash().get(uuid, TokenUtils.REFRESH_TOKEN);

        if (auth.isEmpty() || this.isInvalidToken(uuid, refreshToken)) {
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

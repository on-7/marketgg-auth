package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.jwt.RefreshToken;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.util.Date;
import java.util.Objects;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenGenerator tokenGenerator;

    @Transactional
    @Override
    public void signup(SignupRequestDto signupRequestDto) {

        signupRequestDto.encodingPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        Auth auth = new Auth(signupRequestDto);
        authRepository.save(auth);
    }

    @Override
    public Boolean existsUsername(String username) {
        return authRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsEmail(String email) {
        return authRepository.existsByEmail(email);
    }

    @Override
    public String renewToken(String token) {
        String username = tokenGenerator.getUsername(token);

        RefreshToken refreshToken =
            (RefreshToken) redisTemplate.opsForHash().get(username, REFRESH_TOKEN);

        if (isInvalidToken(username, refreshToken)) {
            return null;
        }

        Authentication authentication = tokenGenerator.getAuthentication(token, username);

        Date issueDate = new Date(System.currentTimeMillis());

        String newJwt = tokenGenerator.generateJwt(authentication, issueDate);
        String newRefreshToken = tokenGenerator.generateRefreshToken(authentication, issueDate);

        redisTemplate.opsForHash().delete(username, REFRESH_TOKEN);
        redisTemplate.opsForHash().put(username, REFRESH_TOKEN, newRefreshToken);

        return newJwt;
    }

    private boolean isInvalidToken(String username, RefreshToken refreshToken) {
        return Objects.isNull(refreshToken) ||
            !Objects.equals(username, refreshToken.getUsername()) ||
            tokenGenerator.isInvalidToken(refreshToken.getToken());
    }

}

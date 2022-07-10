package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import com.nhnacademy.marketgg.auth.jwt.RefreshToken;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private static final String REFRESH_TOKEN = "refresh_token";

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
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
    public String login(LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword());

        Authentication authentication =
            Optional.ofNullable(authenticationManager.authenticate(token))
                    .orElseThrow(LoginFailException::new);

        String jwt = tokenGenerator.generateJwt(authentication);
        String refreshToken = tokenGenerator.generateRefreshToken(authentication);

        redisTemplate.opsForHash()
                     .put(loginRequest.getUsername(), REFRESH_TOKEN,
                         new RefreshToken(loginRequest.getUsername(), refreshToken));

        return jwt;
    }
}

package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.request.SignupRequest;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.UsernameResponse;
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
    public void signup(SignupRequest signupRequest) {

        signupRequest.encodingPassword(passwordEncoder.encode(signupRequest.getPassword()));
        Auth auth = new Auth(signupRequest);
        authRepository.save(auth);
    }

    @Override
    public UsernameResponse existsUsername(String username) {
        return new UsernameResponse(authRepository.existsByUsername(username));
    }

    @Override
    public EmailResponse existsEmail(String email) {
        return new EmailResponse(authRepository.existsByEmail(email));
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

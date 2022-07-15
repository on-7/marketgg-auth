package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.AuthRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.AuthRoleRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;
import com.nhnacademy.marketgg.auth.util.MailUtils;
import com.nhnacademy.marketgg.auth.util.RedisUtils;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.management.relation.RoleNotFoundException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final AuthRoleRepository authRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenGenerator tokenGenerator;
    private final MailUtils mailUtils;
    private final RedisUtils redisUtils;

    @Transactional
    @Override
    public void signup(final SignUpRequest signUpRequest) throws RoleNotFoundException {

        signUpRequest.encodingPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        Auth savedAuth = authRepository.save(new Auth(signUpRequest));

        Long authNo = savedAuth.getId();
        Role role = roleRepository.findByName(Roles.ROLE_USER)
                                  .orElseThrow(
                                      () -> new RoleNotFoundException("해당 권한은 존재 하지 않습니다."));

        AuthRole authRole = new AuthRole(new AuthRole.Pk(authNo, role.getId()), savedAuth, role);
        authRoleRepository.save(authRole);
    }

    @Override
    public void logout(final String token) {
        if (tokenGenerator.isInvalidToken(token)) {
            return;
        }

        String email = tokenGenerator.getEmailFromExpiredToken(token);

        redisTemplate.opsForHash().delete(email, REFRESH_TOKEN);
        long tokenExpireTime = tokenGenerator.getExpireDate(token) - System.currentTimeMillis();
        redisTemplate.opsForValue().set(token, true, tokenExpireTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public String renewToken(final String token) {
        String email = tokenGenerator.getEmailFromExpiredToken(token);

        String refreshToken =
            (String) redisTemplate.opsForHash().get(email, REFRESH_TOKEN);

        if (isInvalidToken(email, refreshToken)) {
            return null;
        }

        Authentication authentication =
            tokenGenerator.getAuthenticationFromExpiredToken(token, email);

        Date issueDate = new Date(System.currentTimeMillis());

        String newJwt = tokenGenerator.generateJwt(authentication, issueDate);
        String newRefreshToken = tokenGenerator.generateRefreshToken(authentication, issueDate);

        redisTemplate.opsForHash().delete(email, REFRESH_TOKEN);
        redisTemplate.opsForHash().put(email, REFRESH_TOKEN, newRefreshToken);

        return newJwt;
    }

    @Override
    public EmailResponse checkEmail(final String email) throws EmailOverlapException {

        if (Boolean.TRUE.equals(authRepository.existsByEmail(email))) {
            throw new EmailOverlapException(email);
        }

        String key = email;
        String value = "emailRedisValue";

        if (mailUtils.sendCheckMail(email)) {
            redisUtils.set(key, value);
        }

        return new EmailResponse(Boolean.FALSE, "해당 이메일은 사용 가능합니다.");
    }

    private boolean isInvalidToken(String email, String refreshToken) {
        return Objects.isNull(refreshToken)
            || tokenGenerator.isInvalidToken(refreshToken)
            || !Objects.equals(email, tokenGenerator.getEmailFromExpiredToken(refreshToken));
    }

}

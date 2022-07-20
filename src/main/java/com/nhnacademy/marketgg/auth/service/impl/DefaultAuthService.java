package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.AuthRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.AuthRoleRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;
import com.nhnacademy.marketgg.auth.util.MailUtils;
import com.nhnacademy.marketgg.auth.util.RedisUtils;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.management.relation.RoleNotFoundException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;
    private final AuthRoleRepository authRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenUtils tokenUtils;
    private final MailUtils mailUtils;
    private final RedisUtils redisUtils;

    @Transactional
    @Override
    public void signup(final SignUpRequest signUpRequest) throws RoleNotFoundException {

        signUpRequest.encodingPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        Auth auth = new Auth(signUpRequest);
        Auth savedAuth = authRepository.save(auth);
        Long authNo = savedAuth.getId();
        Role role = roleRepository.findByName(Roles.ROLE_USER)
                                  .orElseThrow(
                                      () -> new RoleNotFoundException("해당 권한은 존재 하지 않습니다."));
        AuthRole.Pk pk = new AuthRole.Pk(authNo, role.getId());
        AuthRole authRole = new AuthRole(pk, savedAuth, role);
        authRoleRepository.save(authRole);
    }

    @Override
    public void logout(final String token) {
        if (tokenUtils.isInvalidToken(token)) {
            return;
        }

        String email = tokenUtils.getUuidFromExpiredToken(token);

        redisTemplate.opsForHash().delete(email, TokenUtils.REFRESH_TOKEN);
        long tokenExpireTime = tokenUtils.getExpireDate(token) - System.currentTimeMillis();
        redisTemplate.opsForValue().set(token, true, tokenExpireTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public TokenResponse renewToken(final String token) {
        String uuid = tokenUtils.getUuidFromExpiredToken(token);

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
            || tokenUtils.isInvalidToken(refreshToken)
            || !Objects.equals(email, tokenUtils.getUuidFromExpiredToken(refreshToken));
    }

}

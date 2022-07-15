package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignupRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.AuthRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.entity.Roles;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import com.nhnacademy.marketgg.auth.jwt.RefreshToken;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.AuthRoleRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;
import java.util.Date;
import java.util.Objects;
import com.nhnacademy.marketgg.auth.util.MailUtil;
import com.nhnacademy.marketgg.auth.util.RedisUtil;
import java.util.Optional;
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

    private final MailUtil mailUtil;
    private final RedisUtil redisUtil;

    @Transactional
    @Override
    public void signup(final SignupRequest signupRequest) throws RoleNotFoundException {

        signupRequest.encodingPassword(passwordEncoder.encode(signupRequest.getPassword()));

        Auth auth = new Auth(signupRequest);

        Auth savedAuth = authRepository.save(auth);

        Long authNo = savedAuth.getAuthNo();

        Role role = roleRepository.findByName(Roles.ROLE_USER)
                                  .orElseThrow(
                                      () -> new RoleNotFoundException("해당 권한은 존재 하지 않습니다."));

        AuthRole.Pk pk = new AuthRole.Pk(authNo, role.getRoleNo());

        AuthRole authRole = new AuthRole(pk, auth, role);

        authRoleRepository.save(authRole);
    }

    @Override
    public String renewToken(String token) {
        String username = tokenGenerator.getEmail(token);

        RefreshToken refreshToken =
            (RefreshToken) redisTemplate.opsForHash().get(username, REFRESH_TOKEN);

        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                loginRequest.getPassword());

        if (isInvalidToken(username, refreshToken)) {
            return null;
        }

        Authentication authentication = tokenGenerator.getAuthentication(token, username);

        Date issueDate = new Date(System.currentTimeMillis());
        redisTemplate.opsForHash()
                     .put(loginRequest.getEmail(), REFRESH_TOKEN,
                         new RefreshToken(loginRequest.getEmail(), refreshToken));

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

    @Override
    public EmailResponse checkEmail(String email) throws EmailOverlapException {

        log.info(email);
        if (Boolean.TRUE.equals(authRepository.existsByEmail(email))) {
            throw new EmailOverlapException(email);
        }

        String key = email;
        String value = "emailRedisValue";

        if (mailUtil.sendCheckMail(email)) {
            redisUtil.set(key, value);
        }

        return new EmailResponse(Boolean.FALSE, "해당 이메일은 사용 가능합니다.");
    }

}

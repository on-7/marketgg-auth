package com.nhnacademy.marketgg.auth.service.impl;

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
import com.nhnacademy.marketgg.auth.util.MailUtil;
import com.nhnacademy.marketgg.auth.util.RedisUtil;
import java.util.Optional;
import javax.management.relation.RoleNotFoundException;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final AuthenticationManager authenticationManager;
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
    public String login(LoginRequest loginRequest) {

        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                loginRequest.getPassword());

        Authentication authentication =
            Optional.ofNullable(authenticationManager.authenticate(token))
                    .orElseThrow(LoginFailException::new);

        String jwt = tokenGenerator.generateJwt(authentication);
        String refreshToken = tokenGenerator.generateRefreshToken(authentication);

        redisTemplate.opsForHash()
                     .put(loginRequest.getEmail(), REFRESH_TOKEN,
                         new RefreshToken(loginRequest.getEmail(), refreshToken));

        return jwt;
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

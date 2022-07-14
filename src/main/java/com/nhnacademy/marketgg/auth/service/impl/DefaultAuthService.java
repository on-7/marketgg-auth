package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.request.LoginRequest;
import com.nhnacademy.marketgg.auth.dto.response.EmailResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.AuthRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.exception.LoginFailException;
import com.nhnacademy.marketgg.auth.jwt.RefreshToken;
import com.nhnacademy.marketgg.auth.jwt.TokenGenerator;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.AuthRoleRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;

import java.util.Optional;
import javax.management.relation.RoleNotFoundException;
import javax.transaction.Transactional;

import com.nhnacademy.marketgg.auth.util.MailUtil;
import com.nhnacademy.marketgg.auth.util.RedisUtil;
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

    private static final String REFRESH_TOKEN = "refresh_token";
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
    public void signup(final SignUpRequest signUpRequest) throws RoleNotFoundException {

        signUpRequest.encodingPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Auth auth = new Auth(signUpRequest);

        Auth savedAuth = authRepository.save(auth);

        Long authNo = savedAuth.getAuthNo();

        Role role = roleRepository.findByName(Roles.ROLE_USER)
                                  .orElseThrow(() -> new RoleNotFoundException("해당 권한은 존재 하지 않습니다."));

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

        if (Boolean.TRUE.equals(authRepository.existsByEmail(email))) {
            return new EmailResponse(Boolean.TRUE, "해당 이메일은 사용중 입니다.");
        }

        if (!mailUtil.sendCheckMail(email)) {
            throw new EmailOverlapException(email, "해당 이메일은 중복 되었습니다.");
        }

        String key = email;
        String value = "emailRedisValue";

        redisUtil.set(key, value);
        return new EmailResponse(Boolean.FALSE, "해당 이메일은 사용 가능합니다.");
    }
}

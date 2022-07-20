package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.EmailUseRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.ExistEmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.UseEmailResponse;
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
import com.nhnacademy.marketgg.auth.util.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        Auth auth = new Auth(signUpRequest);
        Auth savedAuth = authRepository.save(auth);
        Long authNo = savedAuth.getAuthNo();
        Role role = roleRepository.findByName(Roles.ROLE_USER)
                                  .orElseThrow(
                                          () -> new RoleNotFoundException("해당 권한은 존재 하지 않습니다."));
        AuthRole.Pk pk = new AuthRole.Pk(authNo, role.getId());
        AuthRole authRole = new AuthRole(pk, auth, role);
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
    public UseEmailResponse useEmail(final EmailUseRequest emailUseRequest) {
        if (isExistEmail(emailUseRequest.getEmail())) {
            throw new EmailOverlapException(emailUseRequest.getEmail());
        }

        String key = emailUseRequest.getEmail();
        if(redisUtils.hasKey(key)){
            redisUtils.deleteAuth(key);
        }

        UseEmailResponse useEmailResponse = new UseEmailResponse();
        useEmailResponse.setIsUseEmail(Boolean.FALSE);

        return useEmailResponse;
    }

    /**
     *
     * 입력한 이메일이 중복되지 않으면 Redis 에 key 값에 Email 을 보관합니다.
     * 입력한 이메일이 중복되면 예외처리 합니다.
     *
     * @param emailRequest - 클라이언트가 입력한 이메일 객체 입니다.
     * @return 중복되지 않으면 정상적으로 중복되지 않는다는 값을 가진 Response 객체를 반환합니다.
     */
    @Override
    public ExistEmailResponse checkEmail(final EmailRequest emailRequest) {
        if (!isReferrer(emailRequest) && isExistEmail(emailRequest.getEmail())) {
            throw new EmailOverlapException(emailRequest.getEmail());
        }

        if (isReferrer(emailRequest) && isExistEmail(emailRequest.getEmail())) {
            return new ExistEmailResponse(false);
        }

        // 추천인 없는경우
        String key = emailRequest.getEmail();
        String value = Status.ABLE.toString();

        if (mailUtils.sendMail(emailRequest.getEmail())) {
            redisUtils.set(key, value);
        }

        return new ExistEmailResponse(false);
    }

    private boolean isInvalidToken(String email, String refreshToken) {
        return Objects.isNull(refreshToken)
                || tokenGenerator.isInvalidToken(refreshToken)
                || !Objects.equals(email, tokenGenerator.getEmailFromExpiredToken(refreshToken));
    }

    private boolean isReferrer(EmailRequest emailRequest) {
        return emailRequest.isReferrer();
    }

    private boolean isExistEmail(String email) {
        return Boolean.TRUE.equals(authRepository.existsByEmail(email));
    }

}

package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.dto.request.EmailRequest;
import com.nhnacademy.marketgg.auth.dto.request.EmailUseRequest;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.dto.response.ExistEmailResponse;
import com.nhnacademy.marketgg.auth.dto.response.SignUpResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.dto.response.UseEmailResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.AuthRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.exception.EmailOverlapException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.AuthRoleRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import com.nhnacademy.marketgg.auth.service.AuthService;
import com.nhnacademy.marketgg.auth.util.MailUtils;
import com.nhnacademy.marketgg.auth.util.RedisUtils;
import com.nhnacademy.marketgg.auth.util.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import javax.transaction.Transactional;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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


    /**
     * 회원가입시 추천인 작성후 가입과 추천인 작성없이 가입하는 로직을 구별합니다.
     *
     * @param signUpRequest - 회원가입시 중요정보 입니다.
     * @return SignUpResponse - 회원가입시 marketgg-server 로 정보를 보내기위한 Response DTO 입니다.
     * @throws RoleNotFoundException
     */
    @Transactional
    @Override
    public SignUpResponse signup(final SignUpRequest signUpRequest) throws RoleNotFoundException {

        String referrerUuid = null;
        // 추천인 이메일이 있는경우
        if (signUpRequest.getReferrerEmail() != null) {
            Auth referrerAuth = authRepository.findByEmail(signUpRequest.getReferrerEmail())
                                              .orElseThrow(() -> new AuthNotFoundException(signUpRequest.getReferrerEmail()));

            referrerUuid = referrerAuth.getUuid();
        }

        // 추천인 이메일이 없는 경우.
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
        String uuid = savedAuth.getUuid();
        return new SignUpResponse(uuid, referrerUuid);
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
    public UseEmailResponse useEmail(final EmailUseRequest emailUseRequest) {
        if (isExistEmail(emailUseRequest.getEmail())) {
            throw new EmailOverlapException(emailUseRequest.getEmail());
        }

        String key = emailUseRequest.getEmail();
        if (redisUtils.hasKey(key)) {
            redisUtils.deleteAuth(key);
        }

        UseEmailResponse useEmailResponse = new UseEmailResponse();
        useEmailResponse.setIsUseEmail(Boolean.FALSE);

        return useEmailResponse;
    }

    /**
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
                || tokenUtils.isInvalidToken(refreshToken)
                || !Objects.equals(email, tokenUtils.getUuidFromExpiredToken(refreshToken));
    }

    private boolean isReferrer(EmailRequest emailRequest) {
        return emailRequest.isReferrer();
    }

    private boolean isExistEmail(String email) {
        return Boolean.TRUE.equals(authRepository.existsByEmail(email));
    }

}

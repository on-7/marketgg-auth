package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.request.MemberUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.UuidTokenResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.auth.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.role.RoleRepository;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 정보에 대한 비즈니스 로직을 처리하는 기본 구현체입니다.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultAuthInfoService implements AuthInfoService {

    private final AuthRepository authRepository;
    private final TokenUtils tokenUtils;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     *
     * @author 윤동열
     */
    @Override
    public MemberResponse findAuthByUuid(final String token) {
        String uuid = tokenUtils.getUuidFromToken(token);
        Auth auth = authRepository.findByUuid(uuid)
                                  .orElseThrow(AuthNotFoundException::new);

        return new MemberResponse(auth.getEmail(), auth.getName(), auth.getPhoneNumber());
    }

    /**
     * {@inheritDoc}
     *
     * @author 윤동열
     */
    @Override
    public MemberInfoResponse findMemberInfoByUuid(final String uuid) {
        Auth auth = authRepository.findByUuid(uuid)
                                  .orElseThrow(AuthNotFoundException::new);

        return new MemberInfoResponse(auth.getName(), auth.getEmail(), auth.getPhoneNumber());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MemberNameResponse> findMemberNameList(List<String> uuids) {
        return authRepository.findMembersByUuid(uuids);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public UuidTokenResponse update(final String token, final MemberUpdateRequest memberUpdateRequest) {
        String uuid = tokenUtils.getUuidFromToken(token);
        Auth updatedAuth = authRepository.findByUuid(uuid)
                                         .orElseThrow(AuthNotFoundException::new);
        memberUpdateRequest.encodingPassword(passwordEncoder);

        String updatedUuid = updatedAuth.updateAuth(memberUpdateRequest, passwordEncoder);
        redisTemplate.opsForHash()
                     .delete(uuid, TokenUtils.REFRESH_TOKEN);
        List<SimpleGrantedAuthority> roles = roleRepository.findRolesByAuthId(updatedAuth.getId())
                                                           .stream()
                                                           .map(r -> new SimpleGrantedAuthority(
                                                               r.getName().name()))
                                                           .collect(
                                                               Collectors.toUnmodifiableList());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(updatedUuid, "", roles);

        return new UuidTokenResponse(tokenUtils.saveRefreshToken(redisTemplate, auth), updatedUuid);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void withdraw(final String token, final AuthWithDrawRequest withdrawAuth) {
        String uuid = tokenUtils.getUuidFromToken(token);

        Auth deletedAuth = authRepository.findByUuid(uuid)
                                         .orElseThrow(AuthNotFoundException::new);

        withdrawAuth.encodingPassword(passwordEncoder);

        deletedAuth.deleteAuth(withdrawAuth, passwordEncoder);
    }

}

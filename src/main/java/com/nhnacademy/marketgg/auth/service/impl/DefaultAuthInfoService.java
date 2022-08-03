package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.request.AuthUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.response.MemberInfoResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultAuthInfoService implements AuthInfoService {

    private final AuthRepository authRepository;
    private final TokenUtils tokenUtils;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public MemberResponse findAuthByUuid(final String token) {
        String uuid = tokenUtils.getUuidFromToken(token);
        Auth auth = authRepository.findByUuid(uuid)
                                  .orElseThrow(AuthNotFoundException::new);

        return new MemberResponse(auth.getEmail(), auth.getName(), auth.getPhoneNumber());
    }

    @Override
    public MemberInfoResponse findMemberInfoByUuid(final String uuid) {
        Auth auth = authRepository.findByUuid(uuid)
                                  .orElseThrow(AuthNotFoundException::new);

        return new MemberInfoResponse(auth.getName(), auth.getEmail());
    }

    @Transactional
    @Override
    public TokenResponse update(final String token, final AuthUpdateRequest authUpdateRequest) {

        String uuid = tokenUtils.getUuidFromToken(token);
        Auth updatedAuth = authRepository.findByUuid(uuid)
                                         .orElseThrow(AuthNotFoundException::new);

        updatedAuth.updateAuth(authUpdateRequest);
        redisTemplate.opsForHash()
                     .delete(updatedAuth.getUuid(), TokenUtils.REFRESH_TOKEN);
        List<SimpleGrantedAuthority> roles = roleRepository.findRolesByAuthId(updatedAuth.getId())
                                                           .stream()
                                                           .map(r -> new SimpleGrantedAuthority(
                                                               r.getName().name()))
                                                           .collect(
                                                               Collectors.toUnmodifiableList());

        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(updatedAuth.getUuid(), "", roles);

        return tokenUtils.saveRefreshToken(redisTemplate, auth);
    }

    @Transactional
    @Override
    public void withdraw(String token, final AuthWithDrawRequest authWithDrawRequest) {

        String uuid = tokenUtils.getUuidFromToken(token);

        Auth deletedAuth = authRepository.findByUuid(uuid)
                                         .orElseThrow(AuthNotFoundException::new);

        deletedAuth.deleteAuth(authWithDrawRequest);
    }

}

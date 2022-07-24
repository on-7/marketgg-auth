package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.request.AuthUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.response.TokenResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import com.nhnacademy.marketgg.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultAuthInfoService implements AuthInfoService {

    private final AuthRepository authRepository;

    private final RoleRepository roleRepository;
    private final TokenUtils tokenUtils;

    private final RedisTemplate<String, Object> redisTemplate;

    private final AuthService authService;

    @Transactional
    @Override
    public TokenResponse update(final String token, final AuthUpdateRequest authUpdateRequest)
            throws UnAuthorizationException {

        String uuid = getUuid(token);
        Auth updatedAuth = authRepository.findByUuid(uuid)
                                         .orElseThrow(AuthNotFoundException::new);

        updatedAuth.updateAuth(authUpdateRequest);
        redisTemplate.opsForHash()
                     .delete(updatedAuth.getUuid(), TokenUtils.REFRESH_TOKEN);
        List<SimpleGrantedAuthority> roles = roleRepository.findRolesByAuthId(updatedAuth.getId())
                                                           .stream()
                                                           .map(r -> new SimpleGrantedAuthority(r.getName().name()))
                                                           .collect(Collectors.toUnmodifiableList());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(updatedAuth.getUuid(), "", roles);

        return tokenUtils.saveRefreshToken(redisTemplate, auth);
    }

    @Transactional
    @Override
    public void withdraw(String token
            , final AuthWithDrawRequest authWithDrawRequest) throws UnAuthorizationException {

        Auth deletedAuth = authRepository.findByUuid(getUuid(token))
                                         .orElseThrow(AuthNotFoundException::new);

        deletedAuth.deleteAuth(authWithDrawRequest);
    }

    private String getUuid(String token)
            throws UnAuthorizationException {

        if (Objects.isNull(token)
                || tokenUtils.isInvalidToken(token)) {
            throw new UnAuthorizationException();
        }

        String jwt = token.substring(TokenUtils.BEARER_LENGTH);

        return tokenUtils.getUuidFromToken(jwt);
    }

}

package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.request.AuthUpdateRequest;
import com.nhnacademy.marketgg.auth.dto.request.AuthWithDrawRequest;
import com.nhnacademy.marketgg.auth.dto.response.AuthUpdateResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.exception.UnAuthorizationException;
import com.nhnacademy.marketgg.auth.jwt.TokenUtils;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DefaultAuthInfoService implements AuthInfoService {

    private final AuthRepository authRepository;
    private final TokenUtils tokenUtils;

    @Override
    public AuthUpdateResponse update(String token
            , final AuthUpdateRequest authUpdateRequest) throws UnAuthorizationException {

        Auth updatedAuth = authRepository.findByUuid(validToken(token))
                                         .orElseThrow(AuthNotFoundException::new);

        return updatedAuth.updateAuth(authUpdateRequest);
    }

    @Override
    public void withdraw(String token
            , final AuthWithDrawRequest authWithDrawRequest) throws UnAuthorizationException {

        Auth deletedAuth = authRepository.findByUuid(validToken(token))
                                         .orElseThrow(AuthNotFoundException::new);

        deletedAuth.deleteAuth(authWithDrawRequest);
    }

    private String validToken(String token)
            throws UnAuthorizationException {

        if (Objects.isNull(token)
                || tokenUtils.isInvalidToken(token)) {
            throw new UnAuthorizationException();
        }

        token = token.substring(TokenUtils.BEARER_LENGTH);
        return tokenUtils.getUuidFromToken(token);
    }

}

package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.dto.request.UpdateRequest;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.service.AuthInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthInfoService implements AuthInfoService {

    private final AuthRepository authRepository;

    @Override
    public void update(final UpdateRequest updateRequest) {
        Auth auth = authRepository.findByEmail(updateRequest.getEmail())
                                  .orElseThrow(() -> new AuthNotFoundException(updateRequest.getEmail()));

        Auth updatedAuth = new Auth(updateRequest, auth.getUuid());
        authRepository.save(updatedAuth);
    }

}

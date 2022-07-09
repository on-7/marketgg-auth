package com.nhnacademy.marketgg.auth.service;

import com.nhnacademy.marketgg.auth.dto.SignupRequestDto;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void signup(SignupRequestDto signupRequestDto) {

        signupRequestDto.encodingPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        Auth auth = new Auth(signupRequestDto);
        authRepository.save(auth);
    }

}

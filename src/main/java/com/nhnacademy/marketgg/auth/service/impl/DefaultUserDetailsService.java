package com.nhnacademy.marketgg.auth.service.impl;

import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.Role;
import com.nhnacademy.marketgg.auth.exception.AuthNotFoundException;
import com.nhnacademy.marketgg.auth.jwt.CustomUser;
import com.nhnacademy.marketgg.auth.repository.AuthRepository;
import com.nhnacademy.marketgg.auth.repository.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * email 을 이용하여 사용자를 찾습니다.
 */

@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;
    private final RoleRepository roleRepository;

    /**
     * Spring Security 를 사용하여 로그인 진행 시 email 을 통해 사용자를 찾습니다.
     *
     * @param email - 로그인을 시도하려는 사용자의 Email
     * @return 찾은 사용자를 바탕으로 생성된 UserDetails
     * @throws UsernameNotFoundException 해당 이메일과 일치하는 사용자가 존재하지 않을 때 발생합니다.
     */
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        Auth auth = authRepository.findByEmail(email)
                                  .orElseThrow(() -> new AuthNotFoundException(email));

        List<Role> roles = roleRepository.findRolesByAuthNo(auth.getAuthNo());

        return new CustomUser(auth.getEmail(), auth.getPassword(), roles);
    }

}

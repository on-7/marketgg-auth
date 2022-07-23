package com.nhnacademy.marketgg.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.dto.request.SignUpRequest;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.AuthRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthRepository authRepository;

    @Autowired
    AuthRoleRepository authRoleRepository;

    @Test
    @DisplayName("회원의 권한 조회")
    void testFindRolesByAuthId() {
        Auth auth = getAuth();

        authRepository.save(auth);

        Role role1 = new Role(Roles.ROLE_ADMIN);
        ReflectionTestUtils.setField(role1, "id", 0L);
        Role role2 = new Role(Roles.ROLE_USER);
        ReflectionTestUtils.setField(role2, "id", 1L);

        roleRepository.saveAll(List.of(role1, role2));

        AuthRole.Pk pk1 = new AuthRole.Pk(auth.getId(), role1.getId());
        AuthRole authRole1 = new AuthRole(pk1, auth, role1);

        AuthRole.Pk pk2 = new AuthRole.Pk(auth.getId(), role2.getId());
        AuthRole authRole2 = new AuthRole(pk2, auth, role2);
        authRoleRepository.saveAll(List.of(authRole1, authRole2));

        List<Role> rolesByAuthId = roleRepository.findRolesByAuthId(auth.getId());

        assertThat(rolesByAuthId).hasSize(2);
    }

    private Auth getAuth() {
        SignUpRequest signUpRequest = new SignUpRequest();
        Auth auth = new Auth(signUpRequest);

        ReflectionTestUtils.setField(auth, "email", "email@email.com");
        ReflectionTestUtils.setField(auth, "password", "password");
        ReflectionTestUtils.setField(auth, "name", "name");
        ReflectionTestUtils.setField(auth, "phoneNumber", "01012341234");
        ReflectionTestUtils.setField(auth, "passwordUpdatedAt", LocalDate.now());
        ReflectionTestUtils.setField(auth, "provider", Provider.SELF);

        return auth;
    }

}

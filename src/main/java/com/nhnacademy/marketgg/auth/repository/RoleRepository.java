package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Role DB 접근을 위한 Repository
 */
public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {

    Optional<Role> findByName(Roles name);

}

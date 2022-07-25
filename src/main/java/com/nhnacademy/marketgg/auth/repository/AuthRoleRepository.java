package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.entity.AuthRole;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * AuthRole DB 접근을 위한 Repository
 */
public interface AuthRoleRepository extends JpaRepository<AuthRole, AuthRole.Pk> {
}

package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.constant.Provider;
import com.nhnacademy.marketgg.auth.entity.Auth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Auth DB 접근을 위한 Repository
 */
public interface AuthRepository extends JpaRepository<Auth, Long>, AuthRepositoryCustom {

    Optional<Auth> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Auth> findByUuid(String uuid);

    Optional<Auth> findByEmailAndProvider(String email, Provider provider);

}

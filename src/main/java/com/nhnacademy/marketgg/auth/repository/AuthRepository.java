package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.entity.Auth;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Auth> findByUuid(String uuid);

    boolean existsByUuid(String uuid);
}

package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

    Optional<Auth> findByEmail(String email);

    Boolean existsByEmail(String email);

}

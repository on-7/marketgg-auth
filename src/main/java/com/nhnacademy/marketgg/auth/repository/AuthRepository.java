package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {
}

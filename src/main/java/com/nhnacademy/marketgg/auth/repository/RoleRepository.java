package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.entity.Role;
import java.util.List;
import java.util.Optional;

import com.nhnacademy.marketgg.auth.constant.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r "
         + "FROM AuthRole ar "
         + "    JOIN ar.role r "
         + "        ON ar.id.roleNo = r.id "
         + "WHERE ar.id.authNo = :authNo")
    List<Role> findRolesByAuthNo(Long authNo);

    Optional<Role> findByName(Roles name);
}

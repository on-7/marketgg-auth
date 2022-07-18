package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.entity.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r "
        + "FROM AuthRole ar "
        + "    JOIN ar.role r "
        + "        ON ar.id.roleId = r.id "
        + "WHERE ar.id.authId = :id")
    List<Role> findRolesByAuthId(Long id);

    Optional<Role> findByName(Roles name);
}

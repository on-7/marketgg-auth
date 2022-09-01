package com.nhnacademy.marketgg.auth.repository.role;

import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.entity.Role;
import java.util.List;

/**
 * QueryDsl 을 사용하기 위해 만든 Repository 클래스입니다.
 */
public interface RoleRepositoryCustom {

    List<Role> findRolesByAuthId(Long id);

    List<Roles> findRoleNameByAuthId(Long id);

}

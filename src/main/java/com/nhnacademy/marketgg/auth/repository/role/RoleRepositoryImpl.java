package com.nhnacademy.marketgg.auth.repository.role;

import com.nhnacademy.marketgg.auth.constant.Roles;
import com.nhnacademy.marketgg.auth.entity.QAuthRole;
import com.nhnacademy.marketgg.auth.entity.QRole;
import com.nhnacademy.marketgg.auth.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

/**
 * QueryDsl 을 사용하기 위한 구현체 입니다.
 *
 * @author 윤동열
 */
public class RoleRepositoryImpl extends QuerydslRepositorySupport implements RoleRepositoryCustom {

    public RoleRepositoryImpl() {
        super(Role.class);
    }

    @Override
    public List<Role> findRolesByAuthId(Long id) {

        QRole role = QRole.role;
        QAuthRole authRole = QAuthRole.authRole;

        return from(role)
                .innerJoin(authRole).on(role.id.eq(authRole.id.roleId))
                .where(authRole.id.authId.eq(id))
                .fetch();
    }

    @Override
    public List<Roles> findRoleNameByAuthId(Long id) {

        QAuthRole authRole = QAuthRole.authRole;

        return from(authRole).innerJoin(authRole.role)
                         .where(authRole.auth.id.eq(id))
                         .select(authRole.role.name)
                         .fetch();
    }

}

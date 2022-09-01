package com.nhnacademy.marketgg.auth.repository.auth;

import com.nhnacademy.marketgg.auth.dto.response.AdminMemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.entity.Auth;
import com.nhnacademy.marketgg.auth.entity.QAuth;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

/**
 * Auth 테이블에 대한 QueryDsl 구현체.
 *
 * @author 윤동열
 */
public class AuthRepositoryImpl extends QuerydslRepositorySupport implements AuthRepositoryCustom {

    public AuthRepositoryImpl() {
        super(Auth.class);
    }

    @Override
    public List<MemberNameResponse> findMembersByUuid(List<String> uuids) {
        QAuth auth = QAuth.auth;

        return from(auth)
                .where(auth.uuid.in(uuids))
                .select(Projections.constructor(MemberNameResponse.class, auth.uuid, auth.name))
                .fetch();
    }

    @Override
    public Page<AdminMemberResponse> findMembers(Pageable pageable) {
        QAuth auth = QAuth.auth;

        QueryResults<AdminMemberResponse> result = from(auth).where(auth.deletedAt.isNull())
                                                             .select(Projections.constructor(
                                                                 AdminMemberResponse.class,
                                                                 auth.id, auth.uuid,
                                                                 auth.email, auth.name,
                                                                 auth.phoneNumber,
                                                                 auth.createdAt))
                                                             .orderBy(auth.createdAt.desc())
                                                             .offset(pageable.getOffset())
                                                             .limit(pageable.getPageSize() + 10L)
                                                             .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    @Override
    public boolean isExistNotWithdraw(String uuid) {
        QAuth auth = QAuth.auth;

        return from(auth).where(auth.uuid.eq(uuid)
                                         .and(auth.deletedAt.isNull()))
                         .fetchOne() != null;
    }

}

package com.nhnacademy.marketgg.auth.repository.auth;

import com.nhnacademy.marketgg.auth.dto.response.AdminMemberResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import com.nhnacademy.marketgg.auth.dto.response.MemberResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Auth 테이블에 대해 QueryDsl 을 사용하기 위한 인터페이스입니다.
 *
 * @author 윤동열
 */
@NoRepositoryBean
public interface AuthRepositoryCustom {

    List<MemberNameResponse> findMembersByUuid(List<String> uuids);

    Page<AdminMemberResponse> findMembers(Pageable pageable);

    boolean isExistNotWithdraw(String uuid);

}

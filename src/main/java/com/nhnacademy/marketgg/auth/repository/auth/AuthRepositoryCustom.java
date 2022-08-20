package com.nhnacademy.marketgg.auth.repository.auth;

import com.nhnacademy.marketgg.auth.dto.response.MemberNameResponse;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Auth 테이블에 대해 QueryDsl 을 사용하기 위한 인터페이스입니다.
 *
 * @author 윤동열
 */
@NoRepositoryBean
public interface AuthRepositoryCustom {

    List<MemberNameResponse> findMembersByUuid(List<String> uuids);

    boolean isExistNotWithdraw(String uuid);

}

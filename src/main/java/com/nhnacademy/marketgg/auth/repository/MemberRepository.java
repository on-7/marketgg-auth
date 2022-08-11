package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.entity.Member;
import java.util.Optional;

/**
 * 사용자 정보를 다루기 위한 Repository
 *
 * @author 윤동열
 */
public interface MemberRepository {

    Optional<Member> login(String username, String password);

}

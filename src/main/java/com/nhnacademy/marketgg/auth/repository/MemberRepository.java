package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.entity.Member;
import java.util.Optional;

public interface MemberRepository {

    Optional<Member> login(String username, String password);

}

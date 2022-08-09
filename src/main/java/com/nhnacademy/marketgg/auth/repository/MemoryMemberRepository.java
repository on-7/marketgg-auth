package com.nhnacademy.marketgg.auth.repository;

import com.nhnacademy.marketgg.auth.entity.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

// TODO 3: 회원 정보를 저장하는 Repository.
// 세션을 DB 로 하다보니 회원 정보를 저장하는 DB 는 간단히 메모리를 사용하여 구성하였다.
@Repository
public class MemoryMemberRepository implements MemberRepository {

    private final Map<String, Member> memory;

    public MemoryMemberRepository() {
        this.memory = new HashMap<>();

        memory.put("admin", new Member("admin", "1234"));
        memory.put("user", new Member("user", "1234"));
    }

    @Override
    public Optional<Member> login(String username, String password) {
        Member member = memory.get(username);
        if (member != null && member.getPassword().equals(password)) {
            return Optional.of(member);
        }
        return Optional.empty();
    }

}

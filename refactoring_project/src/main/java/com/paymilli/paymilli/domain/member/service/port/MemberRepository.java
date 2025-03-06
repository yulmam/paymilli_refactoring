package com.paymilli.paymilli.domain.member.service.port;

import com.paymilli.paymilli.domain.member.domain.Member;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository {
    Optional<Member> findByLoginId(String loginId);

    List<Member> findByLoginIdOrEmail(String loginid, String email);

    Optional<Member> findByIdAndDeleted(UUID id, boolean deleted);

    void save(Member member);

    Optional<Member> findById(UUID memberId);
}

package com.paymilli.paymilli.domain.member.infrastructure;

import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {

    Optional<MemberEntity> findByMemberId(String memberId);

    List<MemberEntity> findByMemberIdOrEmail(String memberId, String email);

    Optional<MemberEntity> findByIdAndDeleted(UUID uuid, boolean deleted);
}
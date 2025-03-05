package com.paymilli.paymilli.domain.member.infrastructure;

import com.paymilli.paymilli.domain.card.infrastructure.JPACardRepository;
import com.paymilli.paymilli.domain.member.domain.Member;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.member.service.port.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Component
@AllArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final JPAMemberRepository jpaMemberRepository;
    private final JPACardRepository jpaCardRepository;

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return jpaMemberRepository.findByLoginId(loginId)
                .map(MemberEntity::toModel);
    }

    @Override
    public List<Member> findByLoginIdOrEmail(String loginId, String email) {
        return jpaMemberRepository.findByLoginIdOrEmail(loginId, email).stream()
                .map(MemberEntity::toModel)
                .toList();
    }


    @Override
    public Optional<Member> findByIdAndDeleted(UUID id, boolean deleted) {
        return jpaMemberRepository.findByIdAndDeleted(id, deleted)
                .map(MemberEntity::toModel);
    }

    @Override
    public void save(Member member) {
        jpaMemberRepository.save(MemberEntity.fromModel(member, jpaCardRepository.getReferenceById(member.getMainCardId())));
    }
}

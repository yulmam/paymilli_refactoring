package com.paymilli.paymilli.domain.card.infrastructure;

import com.paymilli.paymilli.Member;
import com.paymilli.paymilli.domain.card.domain.Card;
import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.card.service.port.CardRepository;
import com.paymilli.paymilli.domain.member.infrastructure.MemberRepository;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
@AllArgsConstructor
public class CardRepositoryImpl implements CardRepository {

    JPACardRepository jpaCardRepository;
    MemberRepository memberRepository;

    @Override
    public Optional<Card> findByCardNumberAndMemberId(String cardNumber, UUID memberId) {
        return jpaCardRepository.findByCardNumberAndMemberId(cardNumber, memberId)
                .map(CardEntity::toModel);
    }

    @Override
    public List<Card> findByMemberIdAndDeleted(UUID memberId, boolean deleted) {
        return jpaCardRepository.findByMemberIdAndDeleted(memberId, deleted)
                .stream().map(CardEntity::toModel)
                .toList();
    }

    @Override
    public void deleteByIdAndMemberId(UUID cardId, UUID memberId) {
        jpaCardRepository.deleteByIdAndMemberId(cardId, memberId);
    }

    @Override
    public Optional<Card> findByIdAndMemberIdAndDeleted(UUID id, UUID memberId, boolean deleted) {
        return jpaCardRepository.findByIdAndMemberIdAndDeleted(id, memberId, deleted)
                .map(CardEntity::toModel);
    }

    @Override
    public Optional<Card> findByIdAndMemberId(UUID id, UUID memberId) {
        return jpaCardRepository.findByIdAndMemberId(id, memberId)
                .map(CardEntity::toModel);
    }

    @Override
    public void save(Card card) {
        MemberEntity memberEntity = memberRepository.getReferenceById(card.getMemberId());
        jpaCardRepository.save(CardEntity.fromModel(card, memberEntity));
    }
}

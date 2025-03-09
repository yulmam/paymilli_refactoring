package com.paymilli.paymilli.domain.card.infrastructure;

import com.paymilli.paymilli.domain.card.domain.Card;
import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.card.service.port.CardRepository;
import com.paymilli.paymilli.domain.member.infrastructure.JPAMemberRepository;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
public class CardRepositoryImpl implements CardRepository {

    private final JPACardRepository jpaCardRepository;
    private final JPAMemberRepository jpaMemberRepository;

    @Autowired
    public CardRepositoryImpl(JPACardRepository jpaCardRepository, JPAMemberRepository jpaMemberRepository){
        this.jpaCardRepository = jpaCardRepository;
        this.jpaMemberRepository = jpaMemberRepository;
    }

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
        MemberEntity memberEntity = jpaMemberRepository.getReferenceById(card.getMemberId());
        jpaCardRepository.save(CardEntity.fromModel(card, memberEntity));
    }

    @Override
    public Optional<Card> findMainCardByMemberId(UUID memberId) {
        return jpaCardRepository.findMainCardByMemberId(memberId).map(CardEntity::toModel);
    }

    @Override
    public void save(List<Card> cards) {
        List<CardEntity> cardEntities = cards.stream()
                .map(card-> CardEntity.fromModel(card, jpaMemberRepository.getReferenceById(card.getMemberId())))
                .toList();

        jpaCardRepository.saveAll(cardEntities);
    }

    @Override
    public List<Card> findByIdsIn(List<UUID> cardIds) {
        return jpaCardRepository.findByIdIn(cardIds).stream().map(CardEntity::toModel).toList();
    }
}

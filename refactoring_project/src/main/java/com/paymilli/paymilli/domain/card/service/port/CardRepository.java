package com.paymilli.paymilli.domain.card.service.port;

import com.paymilli.paymilli.domain.card.domain.Card;
import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository{

    Optional<Card> findByCardNumberAndMemberId(String cardNumber, UUID memberId);

    List<Card> findByMemberIdAndDeleted(UUID memberId, boolean deleted);

    void deleteByIdAndMemberId(UUID cardId, UUID memberId);

    Optional<Card> findByIdAndMemberIdAndDeleted(UUID id, UUID memberId, boolean deleted);

    Optional<Card> findByIdAndMemberId(UUID id, UUID memberId);

    void save(Card card);

    Optional<Card> findMainCardByMemberId(UUID memberId);

    void save(List<Card> cards);

    List<Card> findByIdsIn(List<UUID> cardIds);
}

package com.paymilli.paymilli.domain.card.repository;

import com.paymilli.paymilli.domain.card.entity.Card;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, UUID> {

    Optional<Card> findByCardNumberAndMemberId(String cardNumber, UUID memberId);

    List<Card> findByMemberIdAndDeleted(UUID memberId, boolean deleted);

    void deleteByIdAndMemberId(UUID cardId, UUID memberId);

    Optional<Card> findByIdAndMemberIdAndDeleted(UUID id, UUID memberId, boolean deleted);

    Optional<Card> findByIdAndMemberId(UUID id, UUID memberId);
}

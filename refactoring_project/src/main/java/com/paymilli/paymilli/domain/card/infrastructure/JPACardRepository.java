package com.paymilli.paymilli.domain.card.infrastructure;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JPACardRepository  extends JpaRepository<CardEntity, UUID> {

    Optional<CardEntity> findByCardNumberAndMemberId(String cardNumber, UUID memberId);

    List<CardEntity> findByMemberIdAndDeleted(UUID memberId, boolean deleted);

    void deleteByIdAndMemberId(UUID cardId, UUID memberId);

    Optional<CardEntity> findByIdAndMemberIdAndDeleted(UUID id, UUID memberId, boolean deleted);

    Optional<CardEntity> findByIdAndMemberId(UUID id, UUID memberId);

    @Query("SELECT c FROM Card c WHERE c.id = (SELECT m.mainCardId FROM Member m WHERE m.id = :memberId)")
    Optional<CardEntity> findMainCardByMemberId(@Param("memberId") UUID memberId);

    List<CardEntity> findByIdIn(List<UUID> cardIds);
}
package com.paymilli.paymilli.domain.card.controller.port;

import com.paymilli.paymilli.domain.card.dto.request.AddCardRequest;
import com.paymilli.paymilli.domain.card.dto.response.CardListResponse;

import java.util.UUID;

public interface CardService {

    void registerCard(AddCardRequest addCardRequest, UUID memberId);

    CardListResponse searchCards(UUID userId);

    void deleteCard(UUID cardId, UUID userId);

    void changeMainCard(UUID cardId, UUID userId);

    void deleteCardByMemberId(UUID memberId);
}

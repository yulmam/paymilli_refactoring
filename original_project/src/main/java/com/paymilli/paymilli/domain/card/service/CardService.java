package com.paymilli.paymilli.domain.card.service;

import com.paymilli.paymilli.domain.card.dto.request.AddCardRequest;
import com.paymilli.paymilli.domain.card.dto.response.CardListResponse;
import com.paymilli.paymilli.domain.card.dto.response.CardResponse;
import java.util.List;
import java.util.UUID;

public interface CardService {

    void registerCard(AddCardRequest addCardRequest, UUID memberId);

    CardListResponse searchCards(UUID userId);

    void deleteCard(UUID cardId, UUID userId);

    void changeMainCard(UUID cardId, UUID userId);
}

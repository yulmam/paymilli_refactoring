package com.paymilli.paymilli.domain.card.domain.vo;

import com.paymilli.paymilli.domain.card.domain.CardInfo;
import com.paymilli.paymilli.domain.card.dto.client.CardValidationResponse;
import com.paymilli.paymilli.domain.card.dto.request.AddCardRequest;
import com.paymilli.paymilli.domain.card.infrastructure.entity.CardType;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CardCreate {
    private final UUID memberId;
    private final String cardNumber;
    private final String cvc;
    private final String expirationDate;
    private final String cardName;
    private final String cardHolderName;
    private final String cardImage;
    private final CardType cardType;

    @Builder
    public CardCreate(AddCardRequest addCardRequest, CardValidationResponse cardValidationResponse, UUID memberId) {
        this.memberId = memberId;
        this.cardNumber = addCardRequest.getCardNumber();
        this.cvc = addCardRequest.getCvc();
        this.expirationDate = addCardRequest.getExpirationDate();
        this.cardName = cardValidationResponse.getCardName();
        this.cardHolderName = addCardRequest.getCardHolderName();
        this.cardImage = cardValidationResponse.getCardImage();
        this.cardType = cardValidationResponse.getCardType();
    }

}

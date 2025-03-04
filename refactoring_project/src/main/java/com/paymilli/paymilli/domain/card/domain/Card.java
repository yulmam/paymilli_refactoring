package com.paymilli.paymilli.domain.card.domain;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardType;
import com.paymilli.paymilli.domain.card.vo.CardCreate;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Getter
public class Card {
    private final UUID id;
    private final UUID memberId;
    private final CardInfo cardInfo;
    private final String cardName;
    private final String cardHolderName;
    private final String cardImage;
    private final CardType cardType;
    private final boolean deleted;

    @Builder
    public Card(UUID id, UUID memberId,String cardNumber, String cvc, String expirationDate, String cardName, String cardHolderName, String cardImage, CardType cardType, boolean deleted) {
        this.id = id;
        this.memberId = memberId;
        this.cardInfo = new CardInfo(cardNumber, cvc, expirationDate);
        this.cardName = cardName;
        this.cardHolderName = cardHolderName;
        this.cardImage = cardImage;
        this.cardType = cardType;
        this.deleted = deleted;
    }

    public Card delete() {
        if(this.deleted)
            throw new IllegalArgumentException("이미 삭제된 card입니다.");
        return Card.builder()
                .id(id)
                .memberId(memberId)
                .cardNumber(cardInfo.getCardNumber())
                .cvc(cardInfo.getCvc())
                .expirationDate(cardInfo.getExpirationDate())
                .cardName(cardName)
                .cardHolderName(cardHolderName)
                .cardImage(cardImage)
                .cardType(cardType)
                .deleted(true)
                .build();
    }

    public Card create() {
        if(!this.deleted)
            throw new IllegalArgumentException("이미 등록된 card입니다.");
        return Card.builder()
                .id(id)
                .memberId(memberId)
                .cardNumber(cardInfo.getCardNumber())
                .cvc(cardInfo.getCvc())
                .expirationDate(cardInfo.getExpirationDate())
                .cardName(cardName)
                .cardHolderName(cardHolderName)
                .cardImage(cardImage)
                .cardType(cardType)
                .deleted(true)
                .build();
    }


    public static Card create(CardCreate cardCreate){
        return Card.builder().build();
    }

}

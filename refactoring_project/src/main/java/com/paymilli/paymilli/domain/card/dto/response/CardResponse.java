package com.paymilli.paymilli.domain.card.dto.response;

import com.paymilli.paymilli.domain.card.constant.CardConstants;
import com.paymilli.paymilli.domain.card.domain.Card;
import com.paymilli.paymilli.domain.card.infrastructure.entity.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private UUID cardId;
    private String cardName;
    private CardType cardType;
    private String cardLastNum;
    private String cardImage;

    public static CardResponse from(Card card){
        return CardResponse.builder()
                .cardId(card.getId())
                .cardName(card.getCardName())
                .cardType(card.getCardType())
                .cardLastNum(card.getCardInfo().getCardNumber().substring(CardConstants.CARD_NUMBER_LENGTH - CardConstants.LAST_DIGITS_COUNT))
                .cardImage(card.getCardImage())
                .build();
    }
}

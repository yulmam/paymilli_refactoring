package com.paymilli.paymilli.domain.card.dto.client;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CardValidationResponse {
    private String cardName;
    private String cardImage;
    private CardType cardType;
}

package com.paymilli.paymilli.domain.card.dto.client;

import com.paymilli.paymilli.domain.card.entity.CardType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
public class CardValidationResponse {
    private String cardName;
    private String cardImage;
    private CardType cardType;
}

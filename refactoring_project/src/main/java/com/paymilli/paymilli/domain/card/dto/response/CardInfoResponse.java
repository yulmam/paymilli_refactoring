package com.paymilli.paymilli.domain.card.dto.response;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CardInfoResponse {
    private String cardImage;
    private String cardName;
    private CardType cardType;
}

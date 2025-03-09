package com.paymilli.paymilli.domain.payment.domain.vo;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CardDetail {
    private String cardName;
    private String cardImg;
    private CardType cardType;
}

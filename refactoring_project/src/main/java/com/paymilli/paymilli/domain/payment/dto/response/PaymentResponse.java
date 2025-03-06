package com.paymilli.paymilli.domain.payment.dto.response;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private String cardId;
    private String cardName;
    private String cardImg;
    private int installment;
    private long chargePrice;
    private CardType cardType;
    private String approveNumber;
}

package com.paymilli.paymilli.domain.payment.dto.request;

import java.util.UUID;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DemandPaymentCardRequest {

    private UUID cardId;
    private int chargePrice;
    private int installment;
}


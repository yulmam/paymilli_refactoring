package com.paymilli.paymilli.domain.payment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;


@Builder
@AllArgsConstructor
public class PaymentDetail {
    private UUID id;
    private UUID cardId;
    //삭제 고민
    private UUID paymentId;
    private long price;
    private int installment;
    private String approveNumber;
    private boolean deleted;
}

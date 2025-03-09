package com.paymilli.paymilli.domain.payment.dto.response;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardType;
import com.paymilli.paymilli.domain.payment.domain.PaymentDetail;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class PaymentDetailResponse {

    private UUID cardId;
    private int installment;
    private long chargePrice;
    private String approveNumber;


    public static PaymentDetailResponse from(PaymentDetail paymentDetail){
        return PaymentDetailResponse.builder()
                .cardId(paymentDetail.getCardId())
                .installment(paymentDetail.getInstallment())
                .chargePrice(paymentDetail.getPrice())
                .approveNumber(paymentDetail.getApproveNumber())
                .build();
    }
}

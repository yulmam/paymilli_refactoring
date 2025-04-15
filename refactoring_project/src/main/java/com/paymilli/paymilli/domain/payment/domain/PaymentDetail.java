package com.paymilli.paymilli.domain.payment.domain;

import com.paymilli.paymilli.domain.payment.domain.vo.CardDetail;
import com.paymilli.paymilli.domain.payment.domain.vo.PaymentDetailCreate;
import com.paymilli.paymilli.global.util.UUIDGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Builder
@Getter
@AllArgsConstructor
public final class PaymentDetail {
    private UUID id;
    private UUID cardId;
    //삭제 고민
    private UUID paymentId;
    private long price;
    private int installment;
    private String approveNumber;
    private boolean deleted;
    private CardDetail cardDetail;

    public static PaymentDetail create(PaymentDetailCreate paymentDetailCreate, UUIDGenerator uuidGenerator){
        return PaymentDetail.builder()
                .id(uuidGenerator.generateUUID())
                .cardId(paymentDetailCreate.getCardId())
                .paymentId(paymentDetailCreate.getPaymentId())
                .installment(paymentDetailCreate.getInstallment())
                .approveNumber(paymentDetailCreate.getApproveNumber())
                .deleted(false)
                .cardDetail(CardDetail.builder()
                        .build())
                .build();
    }

}

package com.paymilli.paymilli.domain.payment.domain.vo;

import com.paymilli.paymilli.domain.payment.domain.PaymentDetail;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentStatus;
import com.paymilli.paymilli.global.util.UUIDGenerator;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class PaymentCreate {
    private final UUID memberId;
    private final long totalPrice;
    private final PaymentStatus status;
    private final LocalDateTime transmissionDate;
    private final String storeName;
    private final String productName;
    private final List<PaymentDetailCreate> paymentDetailCreates;
    private final boolean deleted;

    public PaymentCreate(UUID memberId, long totalPrice, PaymentStatus status, LocalDateTime transmissionDate, String storeName, String productName, List<PaymentDetailCreate> paymentDetailCreates, List<PaymentDetailCreate> paymentDetailCreates1, boolean deleted) {

        this.memberId = memberId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.transmissionDate = transmissionDate;
        this.storeName = storeName;
        this.productName = productName;
        this.paymentDetailCreates = paymentDetailCreates1;
        this.deleted = deleted;
    }
}

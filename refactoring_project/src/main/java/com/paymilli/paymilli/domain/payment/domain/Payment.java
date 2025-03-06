package com.paymilli.paymilli.domain.payment.domain;

import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
public class Payment {
    private final UUID id;
    private final UUID memberId;
    private final long totalPrice;
    private final PaymentStatus status;
    private final LocalDateTime transmissionDate;
    private final String storeName;
    private final String productName;
    private final List<PaymentDetail> paymentDetails;
    private final boolean deleted;
}

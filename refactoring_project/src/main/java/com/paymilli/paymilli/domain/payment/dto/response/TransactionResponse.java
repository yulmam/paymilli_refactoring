package com.paymilli.paymilli.domain.payment.dto.response;

import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class TransactionResponse {

    private String id;
    private String storeName;
    private String detail;
    private long price;
    private LocalDateTime date;
    private PaymentStatus paymentStatus;
}

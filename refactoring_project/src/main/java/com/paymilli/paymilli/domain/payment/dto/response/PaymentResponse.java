package com.paymilli.paymilli.domain.payment.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.paymilli.paymilli.domain.payment.domain.Payment;
import com.paymilli.paymilli.domain.payment.domain.vo.PaymentCreate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {

    private UUID id;
    private String storeName;
    private long price;
    private LocalDateTime date;
    private List<PaymentDetailResponse> paymentDetailResponses;


    public static PaymentResponse from(Payment payment){
        return PaymentResponse.builder()
                .id(payment.getId())
                .storeName(payment.getStoreName())
                .price(payment.getTotalPrice())
                .date(payment.getTransmissionDate())
                .paymentDetailResponses(payment.getPaymentDetails().stream()
                        .map(detail->PaymentDetailResponse.from(detail))
                        .toList())
                .build();
    }


}

package com.paymilli.paymilli.domain.payment.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentGroupResponse {

    private String id;
    private String storeName;
    private int price;
    private LocalDateTime date;
    private List<PaymentResponse> paymentResponse;
}

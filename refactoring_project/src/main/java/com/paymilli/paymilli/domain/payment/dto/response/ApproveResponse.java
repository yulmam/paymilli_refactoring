package com.paymilli.paymilli.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApproveResponse {
    String storeName;
    int totalPrice;
    String detail;
    String refundToken;
}

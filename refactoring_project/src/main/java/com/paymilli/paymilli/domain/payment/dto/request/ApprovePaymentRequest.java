package com.paymilli.paymilli.domain.payment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApprovePaymentRequest {

    private String password;

    public ApprovePaymentRequest(int password) {
        this.password = String.valueOf(password);
    }
}

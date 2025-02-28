package com.paymilli.paymilli.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidatePaymentPasswordResponse {

    private String paymentPasswordToken;
}

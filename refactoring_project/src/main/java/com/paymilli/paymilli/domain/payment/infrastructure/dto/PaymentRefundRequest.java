package com.paymilli.paymilli.domain.payment.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRefundRequest {
	private String approveNumber;
}

package com.paymilli.paymilli.domain.payment.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoResponse {
	private String storeName;
	private long price;
	private String cardNumber; // 끝 4자리
	private String cvc;
	private String expirationDate;
	private String cardType;
	private int installment;
	private String approveNumber;
}

package com.paymilli.paymilli.domain.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor
public class ErrorCardResponse {
	private String cardName;
	private String cardNumber;
	private String cause; // 결제 실패 원인
}

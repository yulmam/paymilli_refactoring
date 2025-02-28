package com.paymilli.paymilli.domain.payment.exception;


public class PaymentCardException extends RuntimeException {
	// 카드사에서 결제 오류
	public PaymentCardException(String message) {
		super(message);
	}
}

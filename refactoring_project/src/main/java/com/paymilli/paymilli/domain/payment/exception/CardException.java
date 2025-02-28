package com.paymilli.paymilli.domain.payment.exception;

import com.paymilli.paymilli.global.exception.BaseResponseStatus;
import com.paymilli.paymilli.global.exception.ClientException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CardException extends RuntimeException {
	private BaseResponseStatus status;
	private ClientException excep;
	private String cardName;
	private String cardLastNumber;
	private String message;
}

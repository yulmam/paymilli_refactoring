package com.paymilli.paymilli.domain.payment.dto.response.cardcompany;

import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefundResponse {
	private HttpStatusCode statusCode;
}

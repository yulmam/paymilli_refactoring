package com.paymilli.paymilli.domain.payment.exception;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.paymilli.paymilli.global.exception.BaseResponseStatus;

import lombok.Getter;

@Getter
public enum PayErrorType {

	LACK_OF_BALANCE("A1014", BaseResponseStatus.LACK_OF_BALANCE),
	EXCEEDED_ONE_TIME("A1016", BaseResponseStatus.EXCEEDED_ONE_TIME),
	EXCEEDED_ONE_DAY("A1017", BaseResponseStatus.EXCEEDED_ONE_DAY);

	private static final Map<String, BaseResponseStatus> CODE_MAP = Collections.unmodifiableMap(
		Stream.of(values()).collect(Collectors.toMap(PayErrorType::getCardCompanyCode, PayErrorType::getResponseStatus)));

	private String cardCompanyCode;
	private BaseResponseStatus responseStatus;

	PayErrorType(String cardCompanyCode, BaseResponseStatus status) {
		this.cardCompanyCode = cardCompanyCode;
		this.responseStatus = status;
	}

	public static BaseResponseStatus of(final String cardcompanyCode) {
		return CODE_MAP.get(cardcompanyCode);
	}
}

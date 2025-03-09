package com.paymilli.paymilli.domain.payment.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paymilli.paymilli.domain.card.dto.response.CardInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
public class MakePaymentResult {
    private boolean success;
    private PaymentInfoResponse response;
    private String errorCode;
    private String errorMessage;


    private MakePaymentResult(boolean success, PaymentInfoResponse response, String errorCode, String errorMessage) {
        this.success = success;
        this.response = response;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;

    }

    public static MakePaymentResult success(PaymentInfoResponse data) {
        return new MakePaymentResult(true, data, null, null);
    }

    public static MakePaymentResult fail(String errorCode, String errorMessage) {
        return new MakePaymentResult(false, null, errorCode, errorMessage);
    }

    public boolean isSuccess(){
        return success;
    }
}

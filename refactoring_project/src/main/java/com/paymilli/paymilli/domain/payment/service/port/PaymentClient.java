package com.paymilli.paymilli.domain.payment.service.port;

import com.paymilli.paymilli.domain.payment.infrastructure.dto.PaymentInfoRequest;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.PaymentRefundRequest;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.MakePaymentResult;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.PaymentRefundResponse;

import java.util.concurrent.CompletableFuture;

public interface PaymentClient {
    CompletableFuture<MakePaymentResult> requestPayment(PaymentInfoRequest paymentInfoRequest);
    PaymentRefundResponse requestRefund(PaymentRefundRequest paymentRefundRequest);
}

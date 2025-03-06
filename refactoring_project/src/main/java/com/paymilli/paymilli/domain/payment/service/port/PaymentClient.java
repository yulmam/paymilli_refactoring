package com.paymilli.paymilli.domain.payment.service.port;

import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentInfoRequest;
import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentRefundRequest;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.MakePaymentResult;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.PaymentInfoResponse;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.PaymentRefundResponse;

import java.util.concurrent.CompletableFuture;

public interface PaymentClient {
    public CompletableFuture<MakePaymentResult> requestPayment(PaymentInfoRequest paymentInfoRequest);
    public PaymentRefundResponse requestRefund(PaymentRefundRequest paymentRefundRequest);
}

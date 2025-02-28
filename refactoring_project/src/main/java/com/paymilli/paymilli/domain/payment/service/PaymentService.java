package com.paymilli.paymilli.domain.payment.service;

import com.paymilli.paymilli.domain.payment.dto.request.ApprovePaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.RefundPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.response.ApproveResponse;
import com.paymilli.paymilli.domain.payment.dto.response.DemandResponse;
import com.paymilli.paymilli.domain.payment.dto.response.PaymentGroupResponse;
import com.paymilli.paymilli.domain.payment.dto.response.SearchPaymentGroupResponse;
import java.time.LocalDate;

public interface PaymentService {

    DemandResponse issueTransactionId(String token, DemandPaymentRequest demandPaymentRequest);

    ApproveResponse approvePayment(String token, String transactionId,
        ApprovePaymentRequest approvePaymentRequest);

    SearchPaymentGroupResponse searchPaymentGroup(String token, int sort, int page, int size,
        LocalDate startDate, LocalDate endDate);

    PaymentGroupResponse getPaymentGroup(String paymentGroupId);

    boolean refundPayment(RefundPaymentRequest refundPaymentRequest);
}

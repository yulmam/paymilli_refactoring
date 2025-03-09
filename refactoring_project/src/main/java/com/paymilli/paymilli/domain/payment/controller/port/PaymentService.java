package com.paymilli.paymilli.domain.payment.controller.port;

import com.paymilli.paymilli.domain.payment.dto.request.ApprovePaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.RefundPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.response.ApproveResponse;
import com.paymilli.paymilli.domain.payment.dto.response.DemandResponse;
import com.paymilli.paymilli.domain.payment.dto.response.PaymentResponse;
import com.paymilli.paymilli.domain.payment.dto.response.SearchPaymentResponse;
import java.time.LocalDate;
import java.util.UUID;

public interface PaymentService {

    DemandResponse issueTransactionId(UUID memberID, DemandPaymentRequest demandPaymentRequest);

    ApproveResponse approvePayment(UUID memberId, String transactionId,
        ApprovePaymentRequest approvePaymentRequest);

    SearchPaymentResponse searchPayment(UUID memberId, int sort, int page, int size,
                                        LocalDate startDate, LocalDate endDate);

    PaymentResponse getPayment(UUID paymentGroupId);

    boolean refundPayment(RefundPaymentRequest refundPaymentRequest);
}

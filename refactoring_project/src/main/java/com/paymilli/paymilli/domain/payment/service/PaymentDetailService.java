package com.paymilli.paymilli.domain.payment.service;

import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentEntity;
import org.springframework.stereotype.Service;

@Service
public interface PaymentDetailService {

    void requestPaymentGroup(PaymentEntity paymentEntity);

    boolean refundPaymentGroup(PaymentEntity paymentEntity);

}

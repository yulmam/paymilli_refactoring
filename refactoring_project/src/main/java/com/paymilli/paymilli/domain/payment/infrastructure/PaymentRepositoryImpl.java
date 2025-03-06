package com.paymilli.paymilli.domain.payment.infrastructure;


import com.paymilli.paymilli.domain.payment.domain.Payment;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PaymentRepositoryImpl implements PaymentRepository{

    private final JPAPaymentRepository jpaPaymentRepository;

    public PaymentRepositoryImpl (JPAPaymentRepository jpaPaymentRepository){
        this.jpaPaymentRepository = jpaPaymentRepository;
    }
    @Override
    public Page<Payment> findByMemberIdAndTransmissionDateBetween(UUID memberId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return jpaPaymentRepository.findByMemberIdAndTransmissionDateBetween(memberId, startDate, endDate, pageable)
                .map(PaymentEntity::toModel);
    }
}

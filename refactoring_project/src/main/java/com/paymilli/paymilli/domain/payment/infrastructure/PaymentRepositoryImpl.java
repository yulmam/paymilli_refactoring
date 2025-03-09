package com.paymilli.paymilli.domain.payment.infrastructure;


import com.paymilli.paymilli.domain.card.infrastructure.JPACardRepository;
import com.paymilli.paymilli.domain.member.infrastructure.JPAMemberRepository;
import com.paymilli.paymilli.domain.payment.domain.Payment;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentRepositoryImpl implements PaymentRepository{

    private final JPAPaymentRepository jpaPaymentRepository;
    private final JPAMemberRepository jpaMemberRepository;
    private final JPACardRepository jpaCardRepository;

    public PaymentRepositoryImpl (JPAPaymentRepository jpaPaymentRepository, JPAMemberRepository jpaMemberRepository, JPACardRepository jpaCardRepository){
        this.jpaPaymentRepository = jpaPaymentRepository;
        this.jpaMemberRepository = jpaMemberRepository;
        this.jpaCardRepository = jpaCardRepository;
    }
    @Override
    public Page<Payment> findByMemberIdAndTransmissionDateBetween(UUID memberId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return jpaPaymentRepository.findByMemberIdAndTransmissionDateBetween(memberId, startDate, endDate, pageable)
                .map(PaymentEntity::toModel);
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return jpaPaymentRepository.findById(paymentId).map(PaymentEntity::toModel);
    }

    @Override
    public void save(Payment payment) {
        jpaPaymentRepository.save(PaymentEntity.fromModel(payment
        ,jpaMemberRepository.getReferenceById(payment.getMemberId()),
                jpaCardRepository, jpaPaymentRepository));
    }
}

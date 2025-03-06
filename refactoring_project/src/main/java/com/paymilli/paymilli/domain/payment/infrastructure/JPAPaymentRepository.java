package com.paymilli.paymilli.domain.payment.infrastructure;

import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface JPAPaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    Page<PaymentEntity> findByMemberIdAndTransmissionDateBetween(UUID memberId,
                                                                 LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
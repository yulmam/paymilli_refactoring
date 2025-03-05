package com.paymilli.paymilli.domain.payment.infrastructure;

import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentEntity;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentGroupRepository extends JpaRepository<PaymentEntity, UUID> {

    Page<PaymentEntity> findByMemberIdAndTransmissionDateBetween(UUID memberId,
                                                                 LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}

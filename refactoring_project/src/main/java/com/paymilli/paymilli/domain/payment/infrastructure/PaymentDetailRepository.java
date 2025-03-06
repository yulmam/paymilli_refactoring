package com.paymilli.paymilli.domain.payment.infrastructure;

import java.util.UUID;

import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailRepository extends JpaRepository<PaymentDetailEntity, UUID> {


}

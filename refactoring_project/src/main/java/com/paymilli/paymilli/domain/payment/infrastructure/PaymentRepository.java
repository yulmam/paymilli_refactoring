package com.paymilli.paymilli.domain.payment.infrastructure;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymilli.paymilli.domain.payment.infrastructure.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {


}

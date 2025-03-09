package com.paymilli.paymilli.domain.payment.domain.vo;

import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentDetailEntity;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class PaymentDetailCreate {
    private UUID cardId;
    private UUID paymentId;
    private long price;
    private int installment;
    private String approveNumber;
    private boolean deleted;
}

package com.paymilli.paymilli.domain.payment.infrastructure.entity;

import com.paymilli.paymilli.domain.card.infrastructure.CardRepositoryImpl;
import com.paymilli.paymilli.domain.card.infrastructure.JPACardRepository;
import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.payment.domain.PaymentDetail;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentDetailRequest;
import com.paymilli.paymilli.domain.payment.dto.response.PaymentDetailResponse;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_detail")
public class PaymentDetailEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "card_id")
    private CardEntity cardEntity;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "payment_id")
    private PaymentEntity paymentEntity;

    // 가격
    @Column(nullable = false)
    private long price;

    // 할부개월
    @Column(nullable = false)
    private int installment;

    // 승인번호
    @Column(nullable = false, name = "approve_number")
    private String approveNumber;

    // 삭제 여부
    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean deleted;


    @Column
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;


    public PaymentDetail toModel(){
        return PaymentDetail.builder()
                .id(id)
                .cardId(cardEntity.getId())
                .paymentId(paymentEntity.getId())
                .price(price)
                .installment(installment)
                .approveNumber(approveNumber)
                .deleted(deleted)
                .build();
    }

    public static PaymentDetailEntity fromModel(PaymentDetail paymentDetail, CardEntity cardEntity, PaymentEntity paymentEntity){
        return PaymentDetailEntity.builder()
                .id(paymentDetail.getId())
                .price(paymentDetail.getPrice())
                .installment(paymentDetail.getInstallment())
                .approveNumber(paymentDetail.getApproveNumber())
                .cardEntity(cardEntity)
                .paymentEntity(paymentEntity)
                .deleted(paymentEntity.isDeleted())
                .build();
    }
}

package com.paymilli.paymilli.domain.payment.infrastructure.entity;

import com.paymilli.paymilli.domain.card.infrastructure.JPACardRepository;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.payment.domain.Payment;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.response.PaymentResponse;
import com.paymilli.paymilli.domain.payment.infrastructure.JPAPaymentRepository;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.List;
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
@AllArgsConstructor
@Table(name = "payment")
public class PaymentEntity {


    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @Column(name = "total_price", nullable = false)
    private long totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus status;

    @Column(name = "transmission_date", nullable = false)
    private LocalDateTime transmissionDate;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @OneToMany(mappedBy = "paymentEntity")
    private List<PaymentDetailEntity> paymentDetailEntities;

    // 삭제 여부
    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean deleted;

    @Column
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;


    public Payment toModel(){
        return Payment.builder()
                .id(id)
                .memberId(memberEntity.getId())
                .totalPrice(totalPrice)
                .status(status)
                .transmissionDate(transmissionDate)
                .storeName(storeName)
                .productName(productName)
                .paymentDetails(paymentDetailEntities.stream()
                        .map(paymentDetailEntity -> paymentDetailEntity.toModel())
                        .toList())
                .deleted(deleted)
                .build();
    }

    public static PaymentEntity fromModel(
            Payment payment,
            MemberEntity memberEntity,
            JPACardRepository jpaCardRepository,
            JPAPaymentRepository jpaPaymentRepository) {

        return PaymentEntity.builder()
                .id(payment.getId())
                .memberEntity(memberEntity)
                .totalPrice(payment.getTotalPrice())
                .status(payment.getStatus())
                .transmissionDate(payment.getTransmissionDate())
                .storeName(payment.getStoreName())
                .productName(payment.getProductName()) // 올바른 변수 사용
                .paymentDetailEntities(convertToPaymentDetailEntities(payment, jpaCardRepository, jpaPaymentRepository))
                .deleted(payment.isDeleted())
                .build();
    }

    private static List<PaymentDetailEntity> convertToPaymentDetailEntities(
            Payment payment,
            JPACardRepository jpaCardRepository,
            JPAPaymentRepository jpaPaymentRepository) {

        return payment.getPaymentDetails().stream()
                .map(detail -> PaymentDetailEntity.fromModel(
                        detail,
                        jpaCardRepository.getReferenceById(detail.getCardId()),
                        jpaPaymentRepository.getReferenceById(payment.getId())))
                .toList();
    }


}

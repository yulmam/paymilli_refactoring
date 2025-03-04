package com.paymilli.paymilli.domain.payment.infrastructure.entity;

import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.response.PaymentGroupResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "payment_group")
public class PaymentGroup {

    @OneToMany(mappedBy = "paymentGroup")
    private final List<Payment> payments = new ArrayList<Payment>();
    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")

    private UUID id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;
    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus status;

    @Column(name = "transmission_date", nullable = false)
    private LocalDateTime transmissionDate;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "product_name", nullable = false)
    private String productName;

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

    @Builder
    public PaymentGroup(int totalPrice, LocalDateTime transmissionDate, String storeName,
        String productName) {
        this.totalPrice = totalPrice;
        this.transmissionDate = transmissionDate;
        this.storeName = storeName;
        this.productName = productName;
    }

    public static PaymentGroup toEntity(DemandPaymentRequest demandPaymentRequest) {
        return PaymentGroup.builder()
            .totalPrice(demandPaymentRequest.getTotalPrice())
            .transmissionDate(LocalDateTime.now())
            .storeName(demandPaymentRequest.getStoreName())
            .productName(demandPaymentRequest.getDetail())
            .build();
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setMemberEntity(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    public PaymentGroupResponse makeResponse() {
        return PaymentGroupResponse.builder()
            .id(id.toString())
            .storeName(storeName)
            .price(totalPrice)
            .date(transmissionDate)
            .paymentResponse(
                payments.stream()
                    .map(Payment::makeResponse)
                    .toList()
            )
            .build();
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setPaymentGroup(this);
    }

}

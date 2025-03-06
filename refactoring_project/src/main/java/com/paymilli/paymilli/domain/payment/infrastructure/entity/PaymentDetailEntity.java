package com.paymilli.paymilli.domain.payment.infrastructure.entity;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.payment.domain.PaymentDetail;
import com.paymilli.paymilli.domain.payment.domain.vo.CardInfoInPaymentDetail;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentDetailRequest;
import com.paymilli.paymilli.domain.payment.dto.response.PaymentResponse;
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
    @JoinColumn(name = "payment_group_id")
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

    public static PaymentDetailEntity toEntity(DemandPaymentDetailRequest demandPaymentDetailRequest) {
        return PaymentDetailEntity.builder()
            .price(demandPaymentDetailRequest.getChargePrice())
            .installment(demandPaymentDetailRequest.getInstallment())
            .build();
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
    }

    public PaymentResponse makeResponse() {
        return PaymentResponse.builder()
            .cardId(id.toString())
            .cardName(cardEntity.getCardName())
            .chargePrice(price)
            .cardType(cardEntity.getCardType())
            .approveNumber(approveNumber)
            .installment(installment)
            .cardImg(cardEntity.getCardImage())
            .build();
    }

    public void setPaymentEntity(PaymentEntity paymentEntity) {
        this.paymentEntity = paymentEntity;
    }

    public void setCardEntity(CardEntity cardEntity) {
        this.cardEntity = cardEntity;
    }



    public PaymentDetail toModel(){
        return PaymentDetail.builder()
                .id(id)
                .cardId(cardEntity.getId())
                .paymentId(paymentEntity.getId())
                .price(price)
                .installment(installment)
                .cardInfoInPaymentDetail(CardInfoInPaymentDetail.builder()
                        .cardId(cardEntity.getId())
                        .cardName(cardEntity.getCardName())
                        .cardImg(cardEntity.getCardImage())
                        .cardType(cardEntity.getCardType())
                        .build())
                .approveNumber(approveNumber)
                .deleted(deleted)
                .build();
    }
}

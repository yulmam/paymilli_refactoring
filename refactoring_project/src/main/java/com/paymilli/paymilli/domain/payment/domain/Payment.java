package com.paymilli.paymilli.domain.payment.domain;

import com.paymilli.paymilli.domain.payment.domain.vo.PaymentCreate;
import com.paymilli.paymilli.domain.payment.domain.vo.PaymentDetailCreate;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentStatus;
import com.paymilli.paymilli.global.util.UUIDGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class Payment {
    private final UUID id;
    private final UUID memberId;
    private final long totalPrice;
    private final PaymentStatus status;
    private final LocalDateTime transmissionDate;
    private final String storeName;
    private final String productName;
    private final List<PaymentDetail> paymentDetails;
    private final boolean deleted;


    public static Payment create(PaymentCreate paymentCreate, UUIDGenerator uuidGenerator){
        return Payment.builder()
                .id(uuidGenerator.generateUUID())
                .memberId(paymentCreate.getMemberId())
                .totalPrice(paymentCreate.getTotalPrice())
                .status(paymentCreate.getStatus())
                .transmissionDate(paymentCreate.getTransmissionDate())
                .storeName(paymentCreate.getStoreName())
                .productName(paymentCreate.getProductName())
                .paymentDetails(createPaymentDetails(paymentCreate.getPaymentDetailCreates(), uuidGenerator))
                .deleted(false)
                .build();
    }

    public Payment refund(){
        return Payment.builder()
                .id(this.id)
                .memberId(this.memberId)
                .totalPrice(this.totalPrice)
                .status(PaymentStatus.REFUND)
                .transmissionDate(this.transmissionDate)
                .storeName(this.storeName)
                .productName(this.productName)
                .paymentDetails(this.paymentDetails)
                .deleted(false)
                .build();
    }
    public Payment cancel(){
        return Payment.builder()
                .id(this.id)
                .memberId(this.memberId)
                .totalPrice(this.totalPrice)
                .status(PaymentStatus.REFUND)
                .transmissionDate(this.transmissionDate)
                .storeName(this.storeName)
                .productName(this.productName)
                .paymentDetails(this.paymentDetails)
                .deleted(true)
                .build();
    }

    public List<PaymentDetail> getPaymentDetails(){
        return Collections.unmodifiableList(paymentDetails);
    }
    private static List<PaymentDetail> createPaymentDetails(List<PaymentDetailCreate> paymentDetailCreates, UUIDGenerator uuidGenerator){
        return paymentDetailCreates.stream().map(create ->{return PaymentDetail.create(create, uuidGenerator);}).toList();
    }
}

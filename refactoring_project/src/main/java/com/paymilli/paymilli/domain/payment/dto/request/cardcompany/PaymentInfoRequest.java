package com.paymilli.paymilli.domain.payment.dto.request.cardcompany;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class PaymentInfoRequest {

    private String storeName;
    private long price;
    private String cardNumber; // 끝 4자리
    private String cvc;
    private String expirationDate;
    private String cardType;
    private int installment;

    public PaymentInfoRequest(String storeName, long price, String cardNumber, String cvc,
        String expirationDate,
        int installment) {
        this.storeName = storeName;
        this.price = price;
        // 끝 4자리
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.expirationDate = expirationDate;
        this.installment = installment;
        this.cardType = installment <= 1 ? "check" : "credit";
    }
}

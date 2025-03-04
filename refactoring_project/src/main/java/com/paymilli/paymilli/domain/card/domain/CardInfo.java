package com.paymilli.paymilli.domain.card.domain;

import lombok.Getter;

@Getter
public class CardInfo {
    private final String cardNumber;
    private final String cvc;
    private final String expirationDate;

    public CardInfo(String cardNumber, String cvc, String expirationDate) {
        if (!isValidCardNumber(cardNumber)) {
            throw new IllegalArgumentException("잘못된 카드 번호 형식입니다.");
        }
        if (!isValidCVC(cvc)) {
            throw new IllegalArgumentException("잘못된 CVC 형식입니다.");
        }
        if (!isValidExpirationDate(expirationDate)) {
            throw new IllegalArgumentException("잘못된 유효기간 형식입니다.");
        }
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.expirationDate = expirationDate;
    }


    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber != null && cardNumber.matches("\\d{16}");
    }

    private boolean isValidCVC(String cvc) {
        return cvc != null && cvc.matches("\\d{3,4}");
    }

    private boolean isValidExpirationDate(String expirationDate) {
        return expirationDate != null && expirationDate.matches("(0[1-9]|1[0-2])/\\d{2}");
    }
}

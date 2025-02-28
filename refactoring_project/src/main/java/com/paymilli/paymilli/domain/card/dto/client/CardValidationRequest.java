package com.paymilli.paymilli.domain.card.dto.client;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardValidationRequest {
    private String cardNumber;
    private String cvc;
    private String expirationDate;
    private String cardPassword;
    private String userKey;
}

package com.paymilli.paymilli.domain.card.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddCardRequest {
    private String cardNumber;
    private String cvc;
    private String expirationDate;
    private String cardHolderName;
    private String cardPassword;
}

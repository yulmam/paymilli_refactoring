package com.paymilli.paymilli.domain.card.dto.client;


import com.paymilli.paymilli.domain.card.dto.request.AddCardRequest;
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


    public static CardValidationRequest fromAddCardRequestAndUserKey(AddCardRequest addCardRequest, String userKey){
        return CardValidationRequest.builder()
                .cardNumber(addCardRequest.getCardNumber())
                .cvc(addCardRequest.getCvc())
                .expirationDate(addCardRequest.getExpirationDate())
                .cardPassword(addCardRequest.getCardPassword())
                .userKey(userKey)
                .build();
    }
}

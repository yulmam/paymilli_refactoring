package com.paymilli.paymilli.domain.card.dto.response;

import com.paymilli.paymilli.domain.card.entity.CardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {
    private UUID cardId;
    private String cardName;
    private CardType cardType;
    private String cardLastNum;
    private String cardImage;
}

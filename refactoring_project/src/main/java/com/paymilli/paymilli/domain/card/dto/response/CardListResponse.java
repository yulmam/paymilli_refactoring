package com.paymilli.paymilli.domain.card.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class CardListResponse {
    UUID mainCardId;
    List<CardResponse> cardList;

    public CardListResponse(){
        mainCardId = null;
        cardList = new ArrayList<>();
    }
}

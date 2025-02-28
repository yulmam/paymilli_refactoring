package com.paymilli.paymilli.domain.card.dto.request;


import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeMainCardRequest {
    private UUID cardId;
}

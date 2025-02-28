package com.paymilli.paymilli.domain.card.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
public class DeleteCardRequest {
    UUID cardId;
}

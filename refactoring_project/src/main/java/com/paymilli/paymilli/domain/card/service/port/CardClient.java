package com.paymilli.paymilli.domain.card.service.port;

import com.paymilli.paymilli.domain.card.dto.client.CardValidationRequest;
import com.paymilli.paymilli.domain.card.dto.client.CardValidationResponse;

public interface CardClient {
    CardValidationResponse validateAndGetCardInfo(CardValidationRequest request);
}

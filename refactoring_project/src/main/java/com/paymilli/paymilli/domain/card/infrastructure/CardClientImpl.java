package com.paymilli.paymilli.domain.card.infrastructure;

import com.paymilli.paymilli.domain.card.dto.client.CardValidationRequest;
import com.paymilli.paymilli.domain.card.dto.client.CardValidationResponse;
import com.paymilli.paymilli.domain.card.service.port.CardClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;

@Component
public class CardClientImpl implements CardClient {
    private final WebClient webClient;

    public CardClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    public CardValidationResponse validateAndGetCardInfo(CardValidationRequest request) {
        return webClient.post()
                .uri("/validation")
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(CardValidationResponse.class)
                .blockOptional()
                .orElseThrow();
    }
}

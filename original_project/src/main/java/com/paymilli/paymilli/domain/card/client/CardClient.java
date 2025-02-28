package com.paymilli.paymilli.domain.card.client;

import com.paymilli.paymilli.domain.card.dto.client.CardValidationRequest;
import com.paymilli.paymilli.domain.card.dto.client.CardValidationResponse;
import com.paymilli.paymilli.domain.card.dto.request.AddCardRequest;
import com.paymilli.paymilli.domain.card.dto.response.CardInfoResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class CardClient {
    private final WebClient webClient;

    public CardClient(WebClient webClient) {
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

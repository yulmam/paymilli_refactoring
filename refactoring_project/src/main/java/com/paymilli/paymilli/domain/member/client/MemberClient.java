package com.paymilli.paymilli.domain.member.client;

import com.paymilli.paymilli.domain.member.dto.client.CardCompLoginRequest;
import com.paymilli.paymilli.domain.member.dto.client.CardCompLoginResponse;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MemberClient {

    private final WebClient webClient;

    public MemberClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public CardCompLoginResponse validateAndGetUserKey(
        CardCompLoginRequest cardCompLoginRequest) {
        return webClient.post()
            .uri("/join")
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .bodyValue(cardCompLoginRequest)
            .retrieve()
            .bodyToMono(CardCompLoginResponse.class)
            .blockOptional()
            .orElseThrow();
    }
}

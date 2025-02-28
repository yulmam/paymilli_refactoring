package com.paymilli.paymilli.global.filter;

import com.paymilli.paymilli.global.exception.ClientException;
import com.paymilli.paymilli.global.filter.dto.ClientExceptionResponse;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class ClientErrorHandler implements ExchangeFilterFunction {
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return next.exchange(request)
            .flatMap(this::handleStatus);
    }

    private Mono<ClientResponse> handleStatus(ClientResponse response) {
        if (response.statusCode().is4xxClientError() || response.statusCode().is5xxServerError()) {
            return response.bodyToMono(ClientExceptionResponse.class)
                .flatMap(errorResponse ->
                    Mono.error(
                        new ClientException(errorResponse.getCode(),
                            errorResponse.getMessage())
                    ));
        } else {
            return Mono.just(response);
        }
    }
}

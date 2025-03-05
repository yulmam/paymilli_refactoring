package com.paymilli.paymilli.domain.payment.client;

import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentInfoRequest;
import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentRefundRequest;
import com.paymilli.paymilli.domain.payment.dto.response.cardcompany.PaymentInfoResponse;
import com.paymilli.paymilli.domain.payment.dto.response.cardcompany.PaymentRefundResponse;
import com.paymilli.paymilli.domain.payment.exception.PaymentCardException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PaymentClient {

    private final WebClient webClient;

    public PaymentClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public PaymentInfoResponse requestPayment(PaymentInfoRequest paymentInfoRequest) {
        log.info("request payment init!!!@@@@@");
        log.info(paymentInfoRequest.toString());

        return webClient.post()
            .uri("/payment")
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .bodyValue(paymentInfoRequest)
            .exchangeToMono(clientResponse -> {
                HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();

                if (statusCode.is2xxSuccessful()) {
                    // 성공적인 응답 처리
                    return clientResponse.bodyToMono(PaymentInfoResponse.class);
                } else if (statusCode == HttpStatus.UNAUTHORIZED) {
                    // 401 Unauthorized 처리 / 한도 초과
                    return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(
                            new PaymentCardException("Over the limit: " + errorBody)));
                } else if (statusCode == HttpStatus.PAYMENT_REQUIRED) {
                    // 402 PaymentDetailEntity Required 처리 / 잔액 부족
                    return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(
                            new PaymentCardException("Lack of balance: " + errorBody)));
                } else {
                    // 예외적인 상태 코드 처리
                    return Mono.error(
                        new IllegalStateException("Unexpected status code: " + statusCode));
                }
            })
            .blockOptional()
            .orElseThrow();
    }

    public PaymentRefundResponse requestRefund(PaymentRefundRequest paymentRefundRequest) {
        return webClient.post()
            .uri("/refund")
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .bodyValue(paymentRefundRequest)
            .exchangeToMono(clientResponse -> {
                HttpStatusCode statusCode = clientResponse.statusCode();  // 상태 코드 가져오기

                // PaymentRefundResponse 객체 생성 및 반환
                return Mono.just(new PaymentRefundResponse(statusCode));
            })
            .block();  // 동기적으로 결과 반환
    }

    public String testRequestToCardCompany() {
        String response = webClient.get()
            .uri("/test/1")
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .retrieve()
            .bodyToMono(String.class)
            .blockOptional()
            .orElseThrow();

        return response;
    }
}

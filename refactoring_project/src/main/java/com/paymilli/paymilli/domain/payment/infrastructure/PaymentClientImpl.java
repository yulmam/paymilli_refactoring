package com.paymilli.paymilli.domain.payment.infrastructure;

import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentInfoRequest;
import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentRefundRequest;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.MakePaymentResult;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.PaymentInfoResponse;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.PaymentRefundResponse;
import com.paymilli.paymilli.domain.payment.exception.PaymentCardException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import com.paymilli.paymilli.domain.payment.service.port.PaymentClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PaymentClientImpl implements PaymentClient {

    private final WebClient webClient;

    public PaymentClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }


    @Async
    public CompletableFuture<MakePaymentResult> requestPayment(PaymentInfoRequest paymentInfoRequest) {
        return webClient.post()
            .uri("/payment")
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(StandardCharsets.UTF_8)
            .bodyValue(paymentInfoRequest)
            .exchangeToMono(clientResponse -> {
                HttpStatus statusCode = (HttpStatus) clientResponse.statusCode();
                if (statusCode.is2xxSuccessful()) {// 성공적인 응답 처리
                    return clientResponse.bodyToMono(PaymentInfoResponse.class)
                            .map(MakePaymentResult::success);
                } else{// 결제 실패시
                    return clientResponse.bodyToMono(String.class)
                        .map(error-> MakePaymentResult.fail(clientResponse.statusCode().toString(), error));
                }
            })
            .toFuture();
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

}

package com.paymilli.paymilli.domain.payment.controller;

import com.paymilli.paymilli.domain.payment.dto.request.ApprovePaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.RefundPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.response.ApproveResponse;
import com.paymilli.paymilli.domain.payment.dto.response.DemandResponse;
import com.paymilli.paymilli.domain.payment.controller.port.PaymentService;
import com.paymilli.paymilli.global.exception.BaseException;
import com.paymilli.paymilli.global.exception.BaseResponse;
import com.paymilli.paymilli.global.exception.BaseResponseStatus;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/demand")
    public ResponseEntity<BaseResponse<DemandResponse>> demandPayment(
        @RequestHeader("Authorization") String token,
        @RequestBody DemandPaymentRequest demandPaymentRequest) {

        validatePrice(demandPaymentRequest);

        return ResponseEntity.ok(new BaseResponse<>(
            paymentService.issueTransactionId(token, demandPaymentRequest)
        ));
    }

    private void validatePrice(DemandPaymentRequest demandPaymentRequest) {
        if (demandPaymentRequest.getTotalPrice() <= 0) {
            throw new BaseException(BaseResponseStatus.PAYMENT_PRICE_INVALID);
        }

        demandPaymentRequest.getPaymentCards()
            .forEach(demandPaymentCardRequest -> {
                if (demandPaymentCardRequest.getChargePrice() <= 0) {
                    throw new BaseException(BaseResponseStatus.PAYMENT_PRICE_INVALID);
                }
            });
    }

    @PostMapping("/approve")
    public ResponseEntity<BaseResponse<ApproveResponse>> approvePayment(
        @RequestHeader("Authorization") String token,
        @RequestHeader("transactionId") String transactionId,
        @RequestBody ApprovePaymentRequest approvePaymentRequest) {

        return ResponseEntity.ok(new BaseResponse<>(
            paymentService.approvePayment(token, transactionId, approvePaymentRequest)));
    }

    @GetMapping
    public ResponseEntity<?> getPaymentsGroup(
        @RequestHeader("Authorization") String token,
        @RequestParam(value = "sort", required = false, defaultValue = "0") int sort,
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "15") int size,
        @RequestParam(value = "startDate", required = false, defaultValue = "1900-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
        @RequestParam(value = "endDate", required = false, defaultValue = "#{T(java.time.LocalDate).now()}") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return new ResponseEntity<>(
            paymentService.searchPaymentGroup(token, sort, page - 1, size, startDate, endDate),
            HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(
        @RequestHeader("Authorization") String token,
        @PathVariable("id") String id) {
        return new ResponseEntity<>(paymentService.getPaymentGroup(id),
            HttpStatus.OK);
    }

    @PostMapping("/refund")
    public ResponseEntity<BaseResponse<?>> refundPayment(
        @RequestHeader("Authorization") String token,
        @RequestBody RefundPaymentRequest refundPaymentRequest) {
        if (paymentService.refundPayment(refundPaymentRequest)) {
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS_REFUND));
        }

        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.REFUND_ERROR));
    }
}

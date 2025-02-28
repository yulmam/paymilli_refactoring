package com.paymilli.paymilli.domain.payment.service;

import com.paymilli.paymilli.domain.card.entity.Card;
import com.paymilli.paymilli.domain.payment.client.PaymentClient;
import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentInfoRequest;
import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentRefundRequest;
import com.paymilli.paymilli.domain.payment.dto.response.cardcompany.PaymentInfoResponse;
import com.paymilli.paymilli.domain.payment.entity.Payment;
import com.paymilli.paymilli.domain.payment.entity.PaymentGroup;
import com.paymilli.paymilli.domain.payment.entity.PaymentStatus;
import com.paymilli.paymilli.domain.payment.exception.CardException;
import com.paymilli.paymilli.domain.payment.exception.PaymentCardException;
import com.paymilli.paymilli.domain.payment.repository.PaymentGroupRepository;
import com.paymilli.paymilli.domain.payment.repository.PaymentRepository;
import com.paymilli.paymilli.global.exception.BaseResponseStatus;
import com.paymilli.paymilli.global.exception.ClientException;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentDetailServiceImpl implements PaymentDetailService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final PaymentGroupRepository paymentGroupRepository;

    @Transactional
    @Override
    public void requestPaymentGroup(PaymentGroup paymentGroup) {

        int paymentIdx = 0;
        String[] approveNumbers = new String[paymentGroup.getPayments().size()];

        paymentGroupRepository.save(paymentGroup);

        try {
            // 순서대로 결제 진행
            for (; paymentIdx < paymentGroup.getPayments().size(); paymentIdx++) {

                Payment payment = paymentGroup.getPayments().get(paymentIdx);

                // 결제
                String approveNumber = requestSinglePayment(
                    paymentGroup.getPayments().get(paymentIdx), paymentGroup.getStoreName());

                // 승인 번호 저장
                payment.setApproveNumber(approveNumber);
                approveNumbers[paymentIdx] = approveNumber;

                paymentRepository.save(payment);
            }

            // 성공시 결제 내역 DB 저장
            paymentGroup.setStatus(PaymentStatus.PAYMENT);

        } catch (ClientException e) {

            log.info("=== ClientException occur ===");
            log.error(e.getMessage(), e);

            // 이전 내역 환불 처리
            for (int i = 0; i < paymentIdx; i++) {
                requestSingleRefund(approveNumbers[i]);
            }

            // 에러 발생 카드
            Payment payment = paymentGroup.getPayments().get(paymentIdx);
            String cardNum = payment.getCard().getCardNumber();

            log.info("=== throw PAYMENT_ERROR to client ===");
            throw new CardException(BaseResponseStatus.PAYMENT_ERROR,
                e,
                payment.getCard().getCardName(),
                cardNum.substring(cardNum.length() - 4),
                e.getMessage());

        }
    }

    private String requestSinglePayment(Payment payment, String storeName) {

        Card card = payment.getCard();

        log.info("card: " + card.getCardNumber() + "@@@@@@@@@@@@@@@");

        // 결제 요청
        PaymentInfoResponse response = paymentClient.requestPayment(
            new PaymentInfoRequest(storeName, payment.getPrice(), card.getCardNumber(),
                card.getCVC(),
                card.getExpirationDate(), payment.getInstallment()));

        log.info("response:" + response.getApproveNumber());
        return response.getApproveNumber();
    }


    @Transactional
    @Override
    public boolean refundPaymentGroup(PaymentGroup paymentGroup) {

        int paymentGroupSize = paymentGroup.getPayments().size();

        for (int i = 0; i < paymentGroupSize; i++) {
            requestSingleRefund(paymentGroup.getPayments().get(i).getApproveNumber());
        }

        // 환불 처리
        paymentGroup.setStatus(PaymentStatus.REFUND);

        // 일단 전부 환불 성공 처리
        return true;
    }

    private boolean requestSingleRefund(String approveNumber) {

        log.info("requestSingleRefund : approveNumber : " + approveNumber);

        try {
            paymentClient.requestRefund(new PaymentRefundRequest(approveNumber));
        } catch (PaymentCardException e) {
            return false;
        }

        return true;
    }

}

package com.paymilli.paymilli.domain.payment.service;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.payment.client.PaymentClient;
import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentInfoRequest;
import com.paymilli.paymilli.domain.payment.dto.request.cardcompany.PaymentRefundRequest;
import com.paymilli.paymilli.domain.payment.dto.response.cardcompany.PaymentInfoResponse;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentDetailEntity;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentEntity;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentStatus;
import com.paymilli.paymilli.domain.payment.exception.CardException;
import com.paymilli.paymilli.domain.payment.exception.PaymentCardException;
import com.paymilli.paymilli.domain.payment.infrastructure.PaymentGroupRepository;
import com.paymilli.paymilli.domain.payment.infrastructure.PaymentRepository;
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
    public void requestPaymentGroup(PaymentEntity paymentEntity) {

        int paymentIdx = 0;
        String[] approveNumbers = new String[paymentEntity.getPaymentDetailEntities().size()];

        paymentGroupRepository.save(paymentEntity);

        try {
            // 순서대로 결제 진행
            for (; paymentIdx < paymentEntity.getPaymentDetailEntities().size(); paymentIdx++) {

                PaymentDetailEntity paymentDetailEntity = paymentEntity.getPaymentDetailEntities().get(paymentIdx);

                // 결제
                String approveNumber = requestSinglePayment(
                    paymentEntity.getPaymentDetailEntities().get(paymentIdx), paymentEntity.getStoreName());

                // 승인 번호 저장
                paymentDetailEntity.setApproveNumber(approveNumber);
                approveNumbers[paymentIdx] = approveNumber;

                paymentRepository.save(paymentDetailEntity);
            }

            // 성공시 결제 내역 DB 저장
            paymentEntity.setStatus(PaymentStatus.PAYMENT);

        } catch (ClientException e) {

            log.info("=== ClientException occur ===");
            log.error(e.getMessage(), e);

            // 이전 내역 환불 처리
            for (int i = 0; i < paymentIdx; i++) {
                requestSingleRefund(approveNumbers[i]);
            }

            // 에러 발생 카드
            PaymentDetailEntity paymentDetailEntity = paymentEntity.getPaymentDetailEntities().get(paymentIdx);
            String cardNum = paymentDetailEntity.getCardEntity().getCardNumber();

            log.info("=== throw PAYMENT_ERROR to client ===");
            throw new CardException(BaseResponseStatus.PAYMENT_ERROR,
                e,
                paymentDetailEntity.getCardEntity().getCardName(),
                cardNum.substring(cardNum.length() - 4),
                e.getMessage());

        }
    }

    private String requestSinglePayment(PaymentDetailEntity paymentDetailEntity, String storeName) {

        CardEntity cardEntity = paymentDetailEntity.getCardEntity();

        log.info("cardEntity: " + cardEntity.getCardNumber() + "@@@@@@@@@@@@@@@");

        // 결제 요청
        PaymentInfoResponse response = paymentClient.requestPayment(
            new PaymentInfoRequest(storeName, paymentDetailEntity.getPrice(), cardEntity.getCardNumber(),
                cardEntity.getCVC(),
                cardEntity.getExpirationDate(), paymentDetailEntity.getInstallment()));

        log.info("response:" + response.getApproveNumber());
        return response.getApproveNumber();
    }


    @Transactional
    @Override
    public boolean refundPaymentGroup(PaymentEntity paymentEntity) {

        int paymentGroupSize = paymentEntity.getPaymentDetailEntities().size();

        for (int i = 0; i < paymentGroupSize; i++) {
            requestSingleRefund(paymentEntity.getPaymentDetailEntities().get(i).getApproveNumber());
        }

        // 환불 처리
        paymentEntity.setStatus(PaymentStatus.REFUND);

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

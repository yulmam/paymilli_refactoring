package com.paymilli.paymilli.domain.payment.service;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.card.service.port.CardRepository;
import com.paymilli.paymilli.domain.member.domain.Member;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.member.jwt.TokenProvider;
import com.paymilli.paymilli.domain.member.service.port.MemberRepository;
import com.paymilli.paymilli.domain.payment.controller.port.PaymentDetailService;
import com.paymilli.paymilli.domain.payment.controller.port.PaymentService;
import com.paymilli.paymilli.domain.payment.domain.Payment;
import com.paymilli.paymilli.domain.payment.dto.request.ApprovePaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentDetailRequest;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.RefundPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.response.ApproveResponse;
import com.paymilli.paymilli.domain.payment.dto.response.DemandResponse;
import com.paymilli.paymilli.domain.payment.dto.response.MetaResponse;
import com.paymilli.paymilli.domain.payment.dto.response.PaymentGroupResponse;
import com.paymilli.paymilli.domain.payment.dto.response.SearchPaymentGroupResponse;
import com.paymilli.paymilli.domain.payment.dto.response.TransactionResponse;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentDetailEntity;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentEntity;
import com.paymilli.paymilli.domain.payment.infrastructure.PaymentRepository;
import com.paymilli.paymilli.global.exception.BaseException;
import com.paymilli.paymilli.global.exception.BaseResponseStatus;
import com.paymilli.paymilli.global.util.RedisUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {


    private final TokenProvider tokenProvider;
    private final RedisUtil redisUtil;
    private final PaymentDetailService paymentDetailService;
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public PaymentServiceImpl(TokenProvider tokenProvider, RedisUtil redisUtil,
                              PaymentDetailService paymentDetailService, CardRepository cardRepository,
                              PaymentRepository paymentRepository, MemberRepository memberRepository,
                              PasswordEncoder passwordEncoder) {
        this.tokenProvider = tokenProvider;
        this.redisUtil = redisUtil;
        this.paymentDetailService = paymentDetailService;
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public DemandResponse issueTransactionId(String token,
        DemandPaymentRequest demandPaymentRequest) {
        String accessToken = tokenProvider.extractAccessToken(token);
        UUID memberId = tokenProvider.getId(accessToken);

        //금액 검증
        long totalPrice = demandPaymentRequest.getPaymentDetailRequests().stream()
            .map(DemandPaymentDetailRequest::getChargePrice)
            .reduce(0, Integer::sum);

        if (totalPrice != demandPaymentRequest.getTotalPrice()) {
            throw new BaseException(BaseResponseStatus.PAYMENT_REQUEST_ERROR);
        }

        //todo 난수 생성 의존성의 역전
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // 6자리 난수 생성 (100000 ~ 999999)

        String transactionId = memberId + "-" + randomNumber;

        redisUtil.saveDataToRedis(transactionId, demandPaymentRequest, 1200 * 1000);

        return new DemandResponse(transactionId);
    }

    @Transactional
    @Override
    public ApproveResponse approvePayment(String token, String transactionId,
        ApprovePaymentRequest approvePaymentRequest) {

        // todo memberId로 빼야함
        String accessToken = tokenProvider.extractAccessToken(token);

        UUID id = tokenProvider.getId(accessToken);

        Member member = memberRepository.findById(id)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

        //결제 비밀번호 확인
        if (!member.checkPaymentPassword(approvePaymentRequest.getPassword(), passwordEncoder)) {
            throw new BaseException(BaseResponseStatus.PAYMENT_PASSWORD_ERROR);
        }

        //redis로 데이터 가져옴
        DemandPaymentRequest demandPaymentRequest = (DemandPaymentRequest) redisUtil.getDataFromRedis(
            transactionId);

        if (demandPaymentRequest == null) {
            throw new BaseException(BaseResponseStatus.TRANSACTION_UNAUTHORIZED);
        }

        List<UUID> cardIds = demandPaymentRequest.getPaymentDetailRequests().stream()
                .map(DemandPaymentDetailRequest::getCardId)
                .collect(Collectors.toList());

        Card

        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // 6자리 난수 생성 (100000 ~ 999999)

        String refundToken = memberEntity.getId() + "-refund-" + randomNumber;

        redisUtil.saveDataToRedis(refundToken, paymentEntity.getId(), 300 * 1000);

        return new ApproveResponse(paymentEntity.getStoreName(), paymentEntity.getTotalPrice(),
            paymentEntity.getProductName(), refundToken);
    }




    @Transactional
    @Override
    public SearchPaymentGroupResponse searchPaymentGroup(String token, int sort, int page, int size,
        LocalDate startDate, LocalDate endDate) {
        log.info("page: {}, size: {}, startDate: {}, endDate: {}", page, size, startDate, endDate);
        String accessToken = tokenProvider.extractAccessToken(token);
        UUID memberId = tokenProvider.getId(accessToken);

        Direction dir = (sort == 0) ? Direction.DESC : Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "transmissionDate"));

        Page<Payment> paymentPage = paymentRepository.findByMemberIdAndTransmissionDateBetween(
            memberId,
            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), pageable);


        MetaResponse meta = MetaResponse.builder()
            .total_count(paymentGroups.getTotalElements())
            .pagable_count(paymentGroups.getTotalPages())
            .build();

        List<TransactionResponse> transactions = paymentGroups.get()
            .map(paymentGroup -> TransactionResponse.builder()
                .id(paymentGroup.getId().toString())
                .storeName(paymentGroup.getStoreName())
                .detail(paymentGroup.getProductName())
                .price(paymentGroup.getTotalPrice())
                .paymentStatus(paymentGroup.getStatus())
                .date(paymentGroup.getTransmissionDate())
                .build())
            .toList();

        return new SearchPaymentGroupResponse(meta, transactions);
    }

    @Transactional
    @Override
    public PaymentGroupResponse getPaymentGroup(String paymentGroupId) {
        UUID paymentGroupUUID = null;

        try {
            paymentGroupUUID = UUID.fromString(paymentGroupId);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.PAYMENT_GROUP_NOT_FOUND);
        }

        PaymentEntity paymentEntity = paymentRepository.findById(paymentGroupUUID)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.PAYMENT_GROUP_NOT_FOUND));

        return paymentEntity.makeResponse();
    }

    @Transactional
    @Override
    public boolean refundPayment(RefundPaymentRequest refundPaymentRequest) {
        log.info("uuid: " + refundPaymentRequest.getRefundToken());
        UUID paymentGroupId = null;

        try {
            paymentGroupId = UUID.fromString((String) redisUtil.getDataFromRedis(
                refundPaymentRequest.getRefundToken()));
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.REFUND_UNAUTHORIZED);
        }

        PaymentEntity paymentEntity = paymentRepository.findById(
                paymentGroupId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.PAYMENT_GROUP_NOT_FOUND));

        return paymentDetailService.refundPaymentGroup(paymentEntity);
    }

    private boolean isNotSamePaymentPassword(MemberEntity memberEntity, String paymentPassword) {
        return !passwordEncoder.matches(paymentPassword, memberEntity.getPaymentPassword());
    }
}

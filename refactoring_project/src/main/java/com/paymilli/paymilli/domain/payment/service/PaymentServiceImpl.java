package com.paymilli.paymilli.domain.payment.service;

import com.paymilli.paymilli.domain.card.domain.Card;
import com.paymilli.paymilli.domain.card.domain.CardInfo;
import com.paymilli.paymilli.domain.card.service.port.CardRepository;
import com.paymilli.paymilli.domain.member.domain.Member;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.member.jwt.TokenProvider;
import com.paymilli.paymilli.domain.member.service.port.MemberRepository;
import com.paymilli.paymilli.domain.payment.controller.port.PaymentService;
import com.paymilli.paymilli.domain.payment.domain.Payment;
import com.paymilli.paymilli.domain.payment.domain.vo.PaymentCreate;
import com.paymilli.paymilli.domain.payment.dto.request.ApprovePaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentDetailRequest;
import com.paymilli.paymilli.domain.payment.dto.request.DemandPaymentRequest;
import com.paymilli.paymilli.domain.payment.dto.request.RefundPaymentRequest;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.PaymentInfoRequest;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.PaymentRefundRequest;
import com.paymilli.paymilli.domain.payment.dto.response.ApproveResponse;
import com.paymilli.paymilli.domain.payment.dto.response.DemandResponse;
import com.paymilli.paymilli.domain.payment.dto.response.MetaResponse;
import com.paymilli.paymilli.domain.payment.dto.response.PaymentResponse;
import com.paymilli.paymilli.domain.payment.dto.response.SearchPaymentResponse;
import com.paymilli.paymilli.domain.payment.dto.response.TransactionResponse;
import com.paymilli.paymilli.domain.payment.infrastructure.dto.MakePaymentResult;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentEntity;
import com.paymilli.paymilli.domain.payment.infrastructure.PaymentRepository;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentStatus;
import com.paymilli.paymilli.domain.payment.service.port.PaymentClient;
import com.paymilli.paymilli.global.exception.BaseException;
import com.paymilli.paymilli.global.exception.BaseResponseStatus;
import com.paymilli.paymilli.global.util.RandomNumberGenerator;
import com.paymilli.paymilli.global.util.RedisUtil;
import com.paymilli.paymilli.global.util.UUIDGenerator;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentClient paymentClient;
    private final UUIDGenerator uuidGenerator;
    private final RandomNumberGenerator randomNumberGenerator;

    public PaymentServiceImpl(TokenProvider tokenProvider, RedisUtil redisUtil, CardRepository cardRepository,
                              PaymentRepository paymentRepository, MemberRepository memberRepository,
                              PasswordEncoder passwordEncoder, PaymentClient paymentClient, UUIDGenerator uuidGenerator, RandomNumberGenerator randomNumberGenerator) {
        this.tokenProvider = tokenProvider;
        this.redisUtil = redisUtil;
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.paymentClient = paymentClient;
        this.uuidGenerator = uuidGenerator;
        this.randomNumberGenerator = randomNumberGenerator;
    }

    @Transactional
    @Override
    public DemandResponse issueTransactionId(UUID memberId,
        DemandPaymentRequest demandPaymentRequest) {

        //금액 검증
        long totalPrice = demandPaymentRequest.getPaymentDetailRequests().stream()
            .map(DemandPaymentDetailRequest::getChargePrice)
            .reduce(0, Integer::sum);

        if (totalPrice != demandPaymentRequest.getTotalPrice()) {
            throw new BaseException(BaseResponseStatus.PAYMENT_REQUEST_ERROR);
        }

        //todo 난수 생성 의존성의 역전
        String transactionId = memberId + "-" + randomNumberGenerator.generateRandomNumber();

        redisUtil.saveDataToRedis(transactionId, demandPaymentRequest, 1200 * 1000);

        return new DemandResponse(transactionId);
    }

    @Transactional
    @Override
    public ApproveResponse approvePayment(UUID memberId, String transactionId,
        ApprovePaymentRequest approvePaymentRequest) {

        // todo memberId로 빼야함
        //결제 비밀번호 확인
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));
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


        //결제 진행
        List<Card> cards = cardRepository.findByIdsIn(cardIds);
        Map<UUID, Card> cardMap = cards.stream()
                .collect(Collectors.toMap(Card::getId, card-> card));

        List<CompletableFuture<MakePaymentResult>> futures = new ArrayList<>();
        for(DemandPaymentDetailRequest request : demandPaymentRequest.getPaymentDetailRequests()){
            Card targetCard = cardMap.get(request.getCardId());
            CardInfo cardInfo = targetCard.getCardInfo();
            CompletableFuture<MakePaymentResult> future = paymentClient.requestPayment(PaymentInfoRequest.builder()
                            .storeName(demandPaymentRequest.getStoreName())
                            .price(request.getChargePrice())
                            .cvc(cardInfo.getCvc())
                            .expirationDate(cardInfo.getExpirationDate())
                            .installment(request.getInstallment())
                    .build());
            futures.add(future);
        }

        List<MakePaymentResult> makePaymentResults =  CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList())).join();


        for(MakePaymentResult makePaymentResult : makePaymentResults){
            if(makePaymentResult.isSuccess()){
                cancelPayments(makePaymentResults);
                throw new BaseException(BaseResponseStatus.PAYMENT_FAIL);
            }
        }



        PaymentCreate paymentCreate = PaymentCreate.builder()
                .memberId(member.getId())
                .totalPrice(demandPaymentRequest.getTotalPrice())
                .status(PaymentStatus.PAYMENT)
                .transmissionDate(LocalDateTime.now())
                .storeName(demandPaymentRequest.getStoreName())
                .productName(demandPaymentRequest.getDetail())
                .build();

        Payment payment = Payment.create(paymentCreate, uuidGenerator);


        paymentRepository.save(payment);
        String refundToken = member.getId() + "-refund-" + randomNumberGenerator.generateRandomNumber();

        redisUtil.saveDataToRedis(refundToken, payment.getId(), 300 * 1000);

        return new ApproveResponse(payment.getStoreName(), payment.getTotalPrice(),
            payment.getProductName(), refundToken);
    }



    @Transactional
    @Override
    public SearchPaymentResponse searchPayment(UUID memberId, int sort, int page, int size,
                                               LocalDate startDate, LocalDate endDate) {

        Direction dir = (sort == 0) ? Direction.DESC : Direction.ASC;
      
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, "transmissionDate"));

        Page<Payment> paymentPage = paymentRepository.findByMemberIdAndTransmissionDateBetween(
            memberId,
            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX), pageable);


        MetaResponse meta = MetaResponse.builder()
            .total_count(paymentPage.getTotalElements())
            .pagable_count(paymentPage.getTotalPages())
            .build();

        List<TransactionResponse> transactions = paymentPage.get()
            .map(paymentGroup -> TransactionResponse.builder()
                .id(paymentGroup.getId().toString())
                .storeName(paymentGroup.getStoreName())
                .detail(paymentGroup.getProductName())
                .price(paymentGroup.getTotalPrice())
                .paymentStatus(paymentGroup.getStatus())
                .date(paymentGroup.getTransmissionDate())
                .build())
            .toList();

        return new SearchPaymentResponse(meta, transactions);
    }

    @Transactional
    @Override
    public PaymentResponse getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.PAYMENT_GROUP_NOT_FOUND));

        return PaymentResponse.from(payment);
    }



    @Transactional
    @Override
    public boolean refundPayment(RefundPaymentRequest refundPaymentRequest) {
        UUID paymentGroupId = null;

        try {
            paymentGroupId = UUID.fromString((String) redisUtil.getDataFromRedis(
                refundPaymentRequest.getRefundToken()));
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.REFUND_UNAUTHORIZED);
        }

        Payment payment = paymentRepository.findById(
                paymentGroupId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.PAYMENT_GROUP_NOT_FOUND));


        payment.getPaymentDetails().stream()
                .forEach(detail -> paymentClient.requestRefund(new PaymentRefundRequest(detail.getApproveNumber())));

        Payment refundPayment = payment.refund();

        paymentRepository.save(refundPayment);
        return true;
    }


    private void cancelPayments(List<MakePaymentResult> makePaymentResults) {
        makePaymentResults.stream()
                .filter(MakePaymentResult::isSuccess) // 성공한 결제만 필터링
                .map(result -> result.getResponse().getApproveNumber()) // 승인 번호 추출
                .forEach(approveNumber -> paymentClient.requestRefund(new PaymentRefundRequest(approveNumber)));//성공한 결제들 취소
    }
}

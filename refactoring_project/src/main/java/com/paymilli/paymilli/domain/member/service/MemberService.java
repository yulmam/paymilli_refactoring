package com.paymilli.paymilli.domain.member.service;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.member.client.MemberClient;
import com.paymilli.paymilli.domain.member.dto.client.CardCompLoginRequest;
import com.paymilli.paymilli.domain.member.dto.client.CardCompLoginResponse;
import com.paymilli.paymilli.domain.member.dto.request.AddMemberRequest;
import com.paymilli.paymilli.domain.member.dto.request.TokenRequest;
import com.paymilli.paymilli.domain.member.dto.request.UpdatePaymentPasswordRequest;
import com.paymilli.paymilli.domain.member.dto.request.ValidatePaymentPasswordRequest;
import com.paymilli.paymilli.domain.member.dto.response.MemberInfoResponse;
import com.paymilli.paymilli.domain.member.dto.response.TokenResponse;
import com.paymilli.paymilli.domain.member.dto.response.ValidatePaymentPasswordResponse;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.member.jwt.TokenProvider;
import com.paymilli.paymilli.domain.member.infrastructure.MemberRepository;
import com.paymilli.paymilli.global.exception.BaseException;
import com.paymilli.paymilli.global.exception.BaseResponseStatus;
import com.paymilli.paymilli.global.util.RedisUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final MemberClient memberClient;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisUtil redisUtil;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
        TokenProvider tokenProvider,
        MemberClient memberClient, AuthenticationManagerBuilder authenticationManagerBuilder,
        RedisUtil redisUtil) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.memberClient = memberClient;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.redisUtil = redisUtil;
    }

    @Transactional
    public void addMember(AddMemberRequest addMemberRequest) {
        Optional<MemberEntity> memberOpt = memberRepository.findByMemberId(
            addMemberRequest.getMemberId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate birthday = LocalDate.parse(addMemberRequest.getBirthday(), formatter);

        if (memberOpt.isPresent()) {
            MemberEntity memberEntity = memberOpt.get();

            if (memberEntity.isDeleted()) {
                memberEntity.create();
                memberEntity.update(addMemberRequest,
                    passwordEncoder.encode(addMemberRequest.getPassword()),
                    passwordEncoder.encode(addMemberRequest.getPaymentPassword()), birthday);

                return;
            }

            throw new BaseException(BaseResponseStatus.MEMBER_ALREADY_EXIST);
        }

        String email = addMemberRequest.getMemberId() + "@ssafy.com";

        CardCompLoginResponse cardCompLoginResponse = memberClient.validateAndGetUserKey(
            new CardCompLoginRequest(email));

        MemberEntity memberEntity = MemberEntity.toEntity(addMemberRequest, cardCompLoginResponse.getUserKey(),
            birthday, passwordEncoder.encode(addMemberRequest.getPassword()),
            passwordEncoder.encode(addMemberRequest.getPaymentPassword()), email);

        memberRepository.save(memberEntity);
    }

    @Transactional
    public MemberInfoResponse getMemberInfo(UUID memberId) {
        return getMemberById(memberId).makeResponse();
    }

    @Transactional
    public void logoutMember(UUID memberId) {
        tokenProvider.removeRefreshToken(memberId);
    }

    @Transactional
    public boolean isSameRefreshToken(String refreshToken) {
        UUID memberId = tokenProvider.getId(refreshToken);

        String savedRefreshToken = tokenProvider.getRefreshToken(memberId.toString());

        System.out.println(savedRefreshToken);
        return savedRefreshToken != null;
    }

    @Transactional
    public TokenResponse issueTokens(TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(tokenRequest.getMemberId(),
                tokenRequest.getPassword());

        Authentication authentication = null;

        try {
            authentication = authenticationManagerBuilder.getObject()
                .authenticate(token);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.LOGIN_ERROR);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String refreshToken = tokenProvider.createRefreshToken(authentication);
        String accessToken = tokenProvider.createAccessToken(authentication);

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public String reissueAccessToken(String refreshToken) {
        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        return tokenProvider.createAccessToken(authentication, refreshToken);
    }

    @Transactional
    public void updatePaymentPassword(UUID memberId,
        String paymentPasswordToken,
        UpdatePaymentPasswordRequest updatePaymentPasswordRequest) {
        MemberEntity memberEntity = getMemberById(memberId);

        if (!redisUtil.hasKey(paymentPasswordToken)) {
            throw new BaseException(BaseResponseStatus.PAYMENT_PASSWORD_TOKEN_NOT_FOUND);
        }

        if (isEqualPassword(memberEntity.getPaymentPassword(),
            updatePaymentPasswordRequest.getPaymentPassword())) {
            throw new BaseException(BaseResponseStatus.PAYMENT_PASSWORD_SAME_ERROR);
        }

        memberEntity.setPaymentPassword(
            passwordEncoder.encode(updatePaymentPasswordRequest.getPaymentPassword()));

        redisUtil.removeDataFromRedis(paymentPasswordToken);
    }

    @Transactional
    public void deleteMember(UUID memberId) {
        MemberEntity memberEntity = getMemberById(memberId);
        memberEntity.delete();

        memberEntity.getCardEntities()
            .forEach(CardEntity::delete);

        tokenProvider.removeRefreshToken(memberId);
    }

    @Transactional
    public MemberEntity getMemberById(UUID memberId) {
        MemberEntity memberEntity = memberRepository.findByIdAndDeleted(memberId, false)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

        if (memberEntity.isDeleted()) {
            log.info("삭제된 회원 조회");
            throw new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND);
        }

        return memberEntity;
    }

    @Transactional
    public ValidatePaymentPasswordResponse validatePaymentPassword(UUID memberId,
        ValidatePaymentPasswordRequest validatePaymentPasswordRequest) {

        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

        log.info(validatePaymentPasswordRequest.getPaymentPassword());
        if (!isEqualPassword(memberEntity.getPaymentPassword(),
            validatePaymentPasswordRequest.getPaymentPassword())) {
            throw new BaseException(BaseResponseStatus.PAYMENT_PASSWORD_ERROR);
        }

        //redis 키 생성
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // 6자리 난수 생성 (100000 ~ 999999)

        String paymentPasswordToken = memberId + "-sequence-" + randomNumber;

        redisUtil.saveDataToRedis(paymentPasswordToken, 1, 300 * 1000);

        return new ValidatePaymentPasswordResponse(paymentPasswordToken);
    }

    public boolean isEqualPassword(String password, String input) {
        return passwordEncoder.matches(input, password);
    }
}

package com.paymilli.paymilli.domain.member.service;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.member.controller.port.MemberService;
import com.paymilli.paymilli.domain.member.domain.Member;
import com.paymilli.paymilli.domain.member.domain.vo.MemberCreate;
import com.paymilli.paymilli.domain.member.domain.vo.MemberUpdate;
import com.paymilli.paymilli.domain.member.infrastructure.MemberClient;
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
import com.paymilli.paymilli.domain.member.infrastructure.JPAMemberRepository;
import com.paymilli.paymilli.domain.member.service.port.MemberRepository;
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
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final MemberClient memberClient;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisUtil redisUtil;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
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
        Optional<Member> optionalMember = memberRepository.findByLoginId(addMemberRequest.getLoginId());

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            //todo memberUpdate 생성
            MemberUpdate memberUpdate = null;
            Member updatedMember = member.update(memberUpdate, passwordEncoder);
            memberRepository.save(updatedMember);
            return;
        }


        CardCompLoginResponse cardCompLoginResponse = memberClient.validateAndGetUserKey(
            new CardCompLoginRequest(makeEmail(addMemberRequest.getLoginId())));

        MemberCreate memberCreate = MemberCreate.builder()
                .loginId(addMemberRequest.getLoginId())
                .rawPassword(addMemberRequest.getPassword())
                .name(addMemberRequest.getName())
                .birthday(addMemberRequest.getBirthday())
                .gender(addMemberRequest.getGender())
                .phone(addMemberRequest.getPhone())
                .rawPaymentPassword(addMemberRequest.getPaymentPassword())
                .userKey(cardCompLoginResponse.getUserKey())
                .build();

        Member member = Member.create(memberCreate, passwordEncoder);

        memberRepository.save(member);
    }

    @Transactional
    public MemberInfoResponse getMemberInfo(UUID memberId) {
        Member member = memberRepository.findByIdAndDeleted(memberId, false)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));
        return MemberInfoResponse.from(member);
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
            new UsernamePasswordAuthenticationToken(tokenRequest.getLoginId(),
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
        Member member= memberRepository.findByIdAndDeleted(memberId, false)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));;
        //토큰이 유효한지 확인
        if (!redisUtil.hasKey(paymentPasswordToken)) {
            throw new BaseException(BaseResponseStatus.PAYMENT_PASSWORD_TOKEN_NOT_FOUND);
        }

        //다시한번 비밀번호 확인
        if (passwordEncoder.matches(updatePaymentPasswordRequest.getPaymentPassword(), member.getPaymentPassword()
            )) {
            throw new BaseException(BaseResponseStatus.PAYMENT_PASSWORD_SAME_ERROR);
        }

        Member updatedMember = member.updatePaymentPassword(updatePaymentPasswordRequest.getPaymentPassword(), passwordEncoder);

        memberRepository.save(updatedMember);

        redisUtil.removeDataFromRedis(paymentPasswordToken);
    }

    @Transactional
    public void deleteMember(UUID memberId) {
        Member member = memberRepository.findByIdAndDeleted(memberId, false)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));
        member.delete();

        memberEntity.getCardEntities()
            .forEach(CardEntity::delete);

        tokenProvider.removeRefreshToken(memberId);
    }

    @Transactional
    private Member getMemberById(UUID memberId) {
        Member member= memberRepository.findByIdAndDeleted(memberId, false)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));


        return member;
    }

    @Transactional
    public ValidatePaymentPasswordResponse validatePaymentPassword(UUID memberId,
        ValidatePaymentPasswordRequest validatePaymentPasswordRequest) {

        MemberEntity memberEntity = JPAMemberRepository.findById(memberId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

        log.info(validatePaymentPasswordRequest.getPaymentPassword());
        if (passwordEncoder.matches(validatePaymentPasswordRequest.getPaymentPassword(), memberEntity.getPaymentPassword())) {
            throw new BaseException(BaseResponseStatus.PAYMENT_PASSWORD_ERROR);
        }

        //redis 키 생성
        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000); // 6자리 난수 생성 (100000 ~ 999999)

        String paymentPasswordToken = memberId + "-sequence-" + randomNumber;

        redisUtil.saveDataToRedis(paymentPasswordToken, 1, 300 * 1000);

        return new ValidatePaymentPasswordResponse(paymentPasswordToken);
    }


    private String makeEmail(String loginId){
        return loginId + "@ssafy.com";
    }
}

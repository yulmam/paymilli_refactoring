package com.paymilli.paymilli.domain.card.service;

import com.paymilli.paymilli.domain.card.domain.Card;
import com.paymilli.paymilli.domain.card.controller.port.CardService;
import com.paymilli.paymilli.domain.card.dto.client.CardValidationRequest;
import com.paymilli.paymilli.domain.card.dto.client.CardValidationResponse;
import com.paymilli.paymilli.domain.card.dto.request.AddCardRequest;
import com.paymilli.paymilli.domain.card.dto.response.CardListResponse;
import com.paymilli.paymilli.domain.card.dto.response.CardResponse;
import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.card.service.port.CardClient;
import com.paymilli.paymilli.domain.card.service.port.CardRepository;
import com.paymilli.paymilli.domain.card.domain.vo.CardCreate;
import com.paymilli.paymilli.domain.member.domain.Member;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.member.infrastructure.JPAMemberRepository;
import com.paymilli.paymilli.domain.member.service.port.MemberRepository;
import com.paymilli.paymilli.global.exception.BaseException;
import com.paymilli.paymilli.global.exception.BaseResponseStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CardServiceImpl implements CardService {
    private final CardClient cardClient;
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    public CardServiceImpl(CardClient cardClient,
                           CardRepository cardRepository,
                           MemberRepository memberRepository) {
        this.cardClient = cardClient;
        this.cardRepository = cardRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void registerCard(AddCardRequest addCardRequest, UUID memberId) {
        Member member = memberRepository.findByIdAndDeleted(memberId, false)
                .orElseThrow(()-> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

        CardValidationResponse response = cardClient.validateAndGetCardInfo(
            CardValidationRequest.fromAddCardRequestAndUserKey(addCardRequest, member.getUserKey())
        );

        Optional<Card> optionalCard = cardRepository.findByCardNumberAndMemberId(
            addCardRequest.getCardNumber(), memberId);


        //soft delete 다시 생성
        if (optionalCard.isPresent()) {
            Card card = optionalCard.get();
            Card createdCard = card.create();
            cardRepository.save(createdCard);
            return;
        }

        CardCreate cardCreate = CardCreate.builder()
                .cardValidationResponse(response)
                .addCardRequest(addCardRequest)
                .memberId(member.getId())
                .build();

        Card card = Card.create(cardCreate);

        cardRepository.save(card);


        // todo : memberEntity 와 member 분리 이후 메인 카드 등록해야한다.
        if(member.getMainCardId() == null){
            Member updatedMember = member.updateMainCardId(card.getId());
            memberRepository.save(updatedMember);
        }
    }

    @Transactional
    public CardListResponse searchCards(UUID memberId) {
        List<Card> cards = cardRepository.findByMemberIdAndDeleted(memberId, false);
        if(cards.isEmpty())
            return new CardListResponse();

        // memberEntity와 member 분리 이후 메인카드 알고리즘 찾기 수행
        //todo 에러 체크
        Member member = memberRepository.findByIdAndDeleted(memberId, false).orElseThrow();

        //mainCard를 list에서 찾기
        //todo domain hash 변경해야함
        int mainCardIdx = member.findMainCardIdx(cards);

        if(mainCardIdx == -1){
            throw new BaseException(BaseResponseStatus.MAIN_CARD_NOT_EXIST);
        }

        List<CardResponse> cardResponses = cards.stream()
            .filter(card -> !card.isDeleted())
            .map(CardResponse::from)
            .collect(Collectors.toList());

        //메인 카드를 제일 앞으로
        if(mainCardIdx != 0)
            Collections.swap(cardResponses, 0, mainCardIdx);

        return new CardListResponse(member.getMainCardId(), cardResponses);
    }

    @Transactional
    public void deleteCard(UUID cardId, UUID memberId) {
        // todo : memberEntity와 member 분리 이후 수행
        Member member =  memberRepository.findByIdAndDeleted(memberId, false).orElseThrow();

        if(member.getMainCardId().equals(cardId)){
            throw new BaseException(BaseResponseStatus.CANT_DELETE_MAIN_CARD);
        }

        Card card= cardRepository.findByIdAndMemberId(cardId, memberId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.CARD_NOT_FOUND));

        Card deletedcard = card.delete();

        cardRepository.save(deletedcard);
    }

    @Transactional
    public void changeMainCard(UUID cardId, UUID memberId) {
        Member member = memberRepository.findByIdAndDeleted(memberId, false).orElseThrow(()-> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

        Card card = cardRepository.findByIdAndMemberIdAndDeleted(cardId, memberId,false)
                .orElseThrow(()-> new BaseException(BaseResponseStatus.CARD_NOT_FOUND));

        if(member.getMainCardId().equals(card.getId())){
            throw new BaseException(BaseResponseStatus.ALREADY_MAIN_CARD);
        }
        // todo : memberEntity와 member 분리 이후 수행


        Member updatedMember = member.updateMainCardId(card.getId());

        memberRepository.save(updatedMember);
    }
}

package com.paymilli.paymilli.domain.card.service;

import com.paymilli.paymilli.domain.card.domain.Card;
import com.paymilli.paymilli.domain.card.infrastructure.CardClient;
import com.paymilli.paymilli.domain.card.controller.port.CardService;
import com.paymilli.paymilli.domain.card.dto.client.CardValidationRequest;
import com.paymilli.paymilli.domain.card.dto.client.CardValidationResponse;
import com.paymilli.paymilli.domain.card.dto.request.AddCardRequest;
import com.paymilli.paymilli.domain.card.dto.response.CardListResponse;
import com.paymilli.paymilli.domain.card.dto.response.CardResponse;
import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.card.service.port.CardRepository;
import com.paymilli.paymilli.domain.card.vo.CardCreate;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import com.paymilli.paymilli.domain.member.infrastructure.MemberRepository;
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
        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(()-> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

        CardValidationResponse response = cardClient.validateAndGetCardInfo(
            CardValidationRequest.fromAddCardRequestAndUserKey(addCardRequest, memberEntity.getUserKey())
        );

        Optional<Card> optionalCard = cardRepository.findByCardNumberAndMemberId(
            addCardRequest.getCardNumber(), memberId);


        //soft delete 다시 생성
        if (optionalCard.isPresent()) {
            Card card = optionalCard.get();
            if (!card.isDeleted()) {
                throw new BaseException(BaseResponseStatus.CARD_ALREADY_REGISTERED);
            }
            card.create();
            cardRepository.save(card);
            return;
        }

        CardCreate cardCreate = CardCreate.builder()
                .cardValidationResponse(response)
                .addCardRequest(addCardRequest)
                .memberId(memberEntity.getId())
                .build();

        Card card = Card.create(cardCreate);

        cardRepository.save(card);


        // todo : memberEntity 와 member 분리 이후 메인 카드 등록해야한다.
//        if(memberEntity.getMainCardEntity() == null){
//            memberEntity.setMainCardEntity(cardEntity);
//        }
    }

    @Transactional
    public CardListResponse searchCards(UUID memberId) {
        List<Card> cards = cardRepository.findByMemberIdAndDeleted(memberId, false);
        if(cards.isEmpty())
            return new CardListResponse();

        // todo : memberEntity와 member 분리 이후 메인카드 알고리즘 찾기 수행
        CardEntity mainCardEntity = memberRepository.findById(memberId).orElseThrow().getMainCardEntity();

        //mainCard를 list에서 찾기
        int mainCardIdx = cards.indexOf(mainCardEntity);

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
        // todo : memberEntity와 member 분리 이후 member 변환
        return new CardListResponse(mainCardEntity.getId(), cardResponses);
    }

    @Transactional
    public void deleteCard(UUID cardId, UUID memberId) {
        // todo : memberEntity와 member 분리 이후 수행
        MemberEntity memberEntity =  memberRepository.findById(memberId).orElseThrow();

        if(memberEntity.getMainCardEntity().getId().equals(cardId)){
            throw new BaseException(BaseResponseStatus.CANT_DELETE_MAIN_CARD);
        }

        Card card= cardRepository.findByIdAndMemberId(cardId, memberId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.CARD_NOT_FOUND));

        if(card.isDeleted()) {
            throw new BaseException(BaseResponseStatus.CARD_ALREADY_DELETED);
        }

        Card deletedcard = card.delete();

        cardRepository.save(deletedcard);
    }

    @Transactional
    public void changeMainCard(UUID cardId, UUID memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId).orElseThrow(()-> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND));

        Card card = cardRepository.findByIdAndMemberIdAndDeleted(cardId, memberId,false)
                .orElseThrow(()-> new BaseException(BaseResponseStatus.CARD_NOT_FOUND));

        if(memberEntity.getMainCardEntity().getId().equals(card.getId())){
            throw new BaseException(BaseResponseStatus.ALREADY_MAIN_CARD);
        }
        // todo : memberEntity와 member 분리 이후 수행
        memberEntity.setMainCardEntity(cardEntity);
    }
}

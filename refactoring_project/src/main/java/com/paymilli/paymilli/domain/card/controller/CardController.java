package com.paymilli.paymilli.domain.card.controller;


import com.paymilli.paymilli.domain.card.dto.request.AddCardRequest;
import com.paymilli.paymilli.domain.card.dto.request.ChangeMainCardRequest;
import com.paymilli.paymilli.domain.card.dto.request.DeleteCardRequest;
import com.paymilli.paymilli.domain.card.dto.response.CardListResponse;
import com.paymilli.paymilli.domain.card.dto.response.CardResponse;
import com.paymilli.paymilli.domain.card.service.CardService;
import com.paymilli.paymilli.domain.member.jwt.TokenProvider;
import com.paymilli.paymilli.global.exception.BaseResponse;
import com.paymilli.paymilli.global.exception.BaseResponseStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/card")
public class CardController {

    private final CardService cardService;
    private final TokenProvider tokenProvider;

    public CardController(CardService cardService, TokenProvider tokenProvider) {
        this.cardService = cardService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping
    public ResponseEntity<?> registerCard(
        @RequestHeader("Authorization") String token,
        @RequestBody AddCardRequest addCardRequest) {
        String accessToken = tokenProvider.extractAccessToken(token);
        UUID memberId = tokenProvider.getId(accessToken);

        cardService.registerCard(addCardRequest, memberId);

        return ResponseEntity.ok(new BaseResponse<Void>(BaseResponseStatus.SUCCESS_CARD_REGISTERED));
    }


    @GetMapping
    public ResponseEntity<CardListResponse> searchCards(
        @RequestHeader("Authorization") String token) {
        String accessToken = tokenProvider.extractAccessToken(token);
        UUID memberId = tokenProvider.getId(accessToken);

        return ResponseEntity.ok(cardService.searchCards(memberId));
    }


    @PutMapping("/maincard")
    public ResponseEntity<?> changeMainCard(@RequestHeader("Authorization") String token, @RequestBody ChangeMainCardRequest request){
        String accessToken = tokenProvider.extractAccessToken(token);
        UUID memberId = tokenProvider.getId(accessToken);

        cardService.changeMainCard(request.getCardId(), memberId);

        return ResponseEntity.ok(new BaseResponse<Void>(BaseResponseStatus.SUCCESS_MAIN_CARD_CHANGED));
    }


    @DeleteMapping
    public ResponseEntity<?> deleteCard(@RequestHeader("Authorization") String token,
        @RequestBody DeleteCardRequest deleteCardRequest) {
        String accessToken = tokenProvider.extractAccessToken(token);
        UUID memberId = tokenProvider.getId(accessToken);

        cardService.deleteCard(deleteCardRequest.getCardId(), memberId);
        return ResponseEntity.ok(new BaseResponse<Void>(BaseResponseStatus.SUCCESS_CARD_DELETED));
    }
}

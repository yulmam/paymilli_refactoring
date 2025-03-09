package com.paymilli.paymilli.global.exception;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 200 : 요청 성공
     */
    SUCCESS(200, "요청에 성공하였습니다."),
    SUCCESS_CREATING(201, "생성에 성공하였습니다."),

    //member 도메인
    SUCCESS_MEMBER_CREATED(201, "정상적으로 가입되었습니다"),

    //cardEntity 도메인
    SUCCESS_MAIN_CARD_CHANGED(200, "메인카드 변경에 성공했습니다."),
    SUCCESS_CARD_REGISTERED(200, "카드 등록에 성공했습니다."),
    SUCCESS_CARD_DELETED(200, "카드 삭제에 성공했습니다."),

    //payment 도메인
    SUCCESS_REFUND(200, "환불 처리가 완료되었습니다."),

    /**
     * 400 잘못된 요청이 왔을 때, Query Parameter나 Request Body가 잘못 왔을 때
     */

    PAYMENT_REQUEST_ERROR(400, "요청된 결제 정보가 부정확합니다."),
    LOGIN_ERROR(400, "아이디 또는 비밀번호가 일치하지 않습니다."),
    ADD_MEMBER_INVALID(400, "입력된 id 또는 비밀번호가 기준에 적합하지 않습니다."),
    PAYMENT_PRICE_INVALID(400, "입력된 가격이 적합하지 않습니다."),
    PAYMENT_PASSWORD_SAME_ERROR(400, "변경하려는 결제 비밀번호가 기존 결제 비밀번호와 동일합니다."),


    /**
     * 401 JWT 관련 에러
     */
    REFRESH_TOKEN_NOT_INPUT(401, "refresh token이 입력되지 않았습니다. 다시 입력해주세요."),
    UNAUTHORIZED(401, "access token이 유효하지 않습니다."),
    REFRESH_UNAUTHORIZED(401, "refresh token이 만료되었습니다. 다시 로그인 해주세요."),
    TRANSACTION_UNAUTHORIZED(401, "transaction ID가 유효하지 않습니다."),
    REFUND_UNAUTHORIZED(401, "refund token이 유효하지 않습니다."),

    LACK_OF_BALANCE(402, "잔액이 부족합니다."),
    EXCEEDED_ONE_TIME(402, "1회 결제 한도를 초과하였습니다."),
    EXCEEDED_ONE_DAY(402, "1일 결제 한도를 초과하였습니다."),
    PAYMENT_FAIL(402, "결제에 실패했습니다."),

    CANT_DELETE_MAIN_CARD(403, "메인 카드는 삭제할 수 없습니다."),
    CARD_ALREADY_REGISTERED(403, "이미 등록된 카드입니다"),


    /**
     * 404 리소스 못 찾는 에러
     */
    MEMBER_NOT_FOUND(404, "멤버를 찾을 수 없습니다."),
    CARD_NOT_FOUND(404, "카드를 찾을 수 없습니다."),
    MAIN_CARD_NOT_EXIST(404, "메인 카드가 존재하지 않습니다."),
    RESOURCE_NOT_FOUND(404, "리소스를 찾을 수 없습니다."),
    PAYMENT_PASSWORD_ERROR(404, "결제 비밀번호가 일치하지 않습니다. 다시 입력해주세요."),
    PAYMENT_GROUP_NOT_FOUND(404, "존재하지 않는 결제 정보입니다."),
    REFUND_ERROR(404, "환불 오류가 발생하였습니다."),
    PAYMENT_PASSWORD_TOKEN_NOT_FOUND(404,
        "PaymentPasswordToken이 유효하지 않습니다. 결제 비밀번호 인증과정을 다시 수행해주세요."),

    /**
     * 이미 존재하는 데이터
     */
    MEMBER_ALREADY_EXIST(409, "이미 가입되어 있는 사용자 입니다."),
    INVALID_USER_JOIN(409, "동일 이메일 혹은 동일 아이디로 가입된 사용자가 있습니다."),
    PAYMENT_ERROR(409, "결제 오류가 발생하였습니다."),
    CARD_ALREADY_DELETED(409, "이미 삭제된 카드입니다."),
    ALREADY_MAIN_CARD(409, "이미 메인카드 입니다."),

    /**
     * 인프라 에러
     */
    SERVER_ERROR(500, "서버 에러");


    private final int code;
    private final String message;

    BaseResponseStatus(int code,
        String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.code = code;
        this.message = message;
    }
}

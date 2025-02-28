package com.paymilli.paymilli.domain.member.exception;

public class MemberNotExistException extends RuntimeException {

    public MemberNotExistException(String message) {
        super(message);
    }
}

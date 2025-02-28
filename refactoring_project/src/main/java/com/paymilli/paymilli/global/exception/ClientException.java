package com.paymilli.paymilli.global.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ClientException extends RuntimeException {
    private final String code;
    public ClientException(String code, String message) {
        super(message);
        this.code = code;
    }
}

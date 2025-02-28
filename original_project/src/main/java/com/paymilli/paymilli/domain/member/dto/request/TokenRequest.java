package com.paymilli.paymilli.domain.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenRequest {

    private String memberId;
    private String password;
}

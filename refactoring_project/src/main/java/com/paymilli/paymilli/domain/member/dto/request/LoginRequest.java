package com.paymilli.paymilli.domain.member.dto.request;

import lombok.Getter;

@Getter
public class LoginRequest {

    private String memberId;
    private String password;

}

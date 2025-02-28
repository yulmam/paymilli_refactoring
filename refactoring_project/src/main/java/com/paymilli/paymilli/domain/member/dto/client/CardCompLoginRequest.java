package com.paymilli.paymilli.domain.member.dto.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CardCompLoginRequest {
    private final String email;
}

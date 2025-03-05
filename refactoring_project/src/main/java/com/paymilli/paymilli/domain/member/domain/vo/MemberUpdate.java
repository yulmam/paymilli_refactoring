package com.paymilli.paymilli.domain.member.domain.vo;

import com.paymilli.paymilli.domain.member.infrastructure.entity.Gender;
import com.paymilli.paymilli.domain.member.infrastructure.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class MemberUpdate {
    private final String loginId;
    private final String rawPassword;
    private final String name;
    private final LocalDate birthday;
    private final Gender gender;
    private final Role role;
    private final String phone;
    private final UUID mainCardId;
    private final String rawPaymentPassword;
    private final String userKey;
    private final boolean deleted;
}

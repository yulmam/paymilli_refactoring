package com.paymilli.paymilli.domain.member.domain.vo;

import com.paymilli.paymilli.domain.member.dto.client.CardCompLoginResponse;
import com.paymilli.paymilli.domain.member.dto.request.AddMemberRequest;
import com.paymilli.paymilli.domain.member.infrastructure.entity.Gender;
import com.paymilli.paymilli.domain.member.infrastructure.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class MemberCreate {
    private final String loginId;
    private final String rawPassword;
    private final String name;
    private final String email;
    private final String birthday;
    private final Gender gender;
    private final Role role;
    private final String phone;
    private final UUID mainCardId;
    private final String rawPaymentPassword;
    private final String userKey;

    @Builder
    public MemberCreate(String loginId, String rawPassword, String name, String birthday, Gender gender, String phone, UUID mainCardId, String rawPaymentPassword, String userKey) {
        this.loginId = loginId;
        this.rawPassword = rawPassword;
        this.name = name;
        this.email = loginId + "@ssafy.com";
        this.birthday = birthday;
        this.gender = gender;
        this.role = Role.USER;
        this.phone = phone;
        this.mainCardId = mainCardId;
        this.rawPaymentPassword = rawPaymentPassword;
        this.userKey = userKey;
    }
}

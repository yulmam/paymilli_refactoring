package com.paymilli.paymilli.domain.member.dto.response;

import com.paymilli.paymilli.domain.member.entity.Gender;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoResponse {

    private String memberId;
    private String name;
    private String email;
    private Gender gender;
    private String phone;
}

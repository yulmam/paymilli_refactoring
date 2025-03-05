package com.paymilli.paymilli.domain.member.dto.response;

import com.paymilli.paymilli.domain.member.domain.Member;
import com.paymilli.paymilli.domain.member.domain.MemberProfile;
import com.paymilli.paymilli.domain.member.infrastructure.entity.Gender;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberInfoResponse {
    private String loginId;
    private String name;
    private String email;
    private Gender gender;
    private String phone;

    public static MemberInfoResponse from(Member member){
        MemberProfile memberProfile = member.getMemberProfile();
        return MemberInfoResponse.builder()
                .loginId(member.getLoginId())
                .name(memberProfile.getName())
                .email(memberProfile.getEmail())
                .gender(memberProfile.getGender())
                .phone(memberProfile.getPhone())
                .build();
    }
}

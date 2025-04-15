package com.paymilli.paymilli.domain.member.domain;

import com.paymilli.paymilli.domain.card.domain.Card;
import com.paymilli.paymilli.domain.member.domain.vo.MemberCreate;
import com.paymilli.paymilli.domain.member.domain.vo.MemberUpdate;
import com.paymilli.paymilli.domain.member.infrastructure.entity.Gender;
import com.paymilli.paymilli.domain.member.infrastructure.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class Member {
    private final UUID id;
    private final String loginId;
    private final String password;
    private final MemberProfile memberProfile;
    private final UUID mainCardId;
    private final String paymentPassword;
    private final String userKey;
    private final boolean deleted;


    @Builder
    public Member(UUID id, String loginId, String password,
                  String name, LocalDate birthday, Gender gender, Role role, String email, String phone,
                  UUID mainCardId, String paymentPassword, String userKey, boolean deleted) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.memberProfile = new MemberProfile(name, birthday, gender, role, email, phone);
        this.mainCardId = mainCardId;
        this.paymentPassword = paymentPassword;
        this.userKey = userKey;
        this.deleted = deleted;
    }


    public static Member create(MemberCreate memberCreate, PasswordEncoder passwordEncoder){
        return Member.builder()
                .loginId(memberCreate.getLoginId())
                .password(passwordEncoder.encode(memberCreate.getRawPassword()))
                .memberProfile(new MemberProfile(memberCreate.getName(), memberCreate.getBirthday(), memberCreate.getGender(), memberCreate.getRole(), memberCreate.getEmail(), memberCreate.getPhone()))
                .mainCardId(memberCreate.getMainCardId())
                .paymentPassword(passwordEncoder.encode(memberCreate.getRawPaymentPassword()))
                .userKey(memberCreate.getUserKey())
                .deleted(false)
                .build();
    }

    public Member update(MemberUpdate memberUpdate, PasswordEncoder passwordEncoder){
        return Member.builder()
                .id(id)
                .loginId(memberUpdate.getLoginId())
                .password(passwordEncoder.encode(memberUpdate.getRawPassword()))
                .memberProfile(new MemberProfile(memberUpdate.getName(), memberUpdate.getBirthday(), memberUpdate.getGender(), memberUpdate.getRole(), id+"@ssafy.com", memberUpdate.getPhone()))
                .mainCardId(memberUpdate.getMainCardId())
                .paymentPassword(passwordEncoder.encode(memberUpdate.getRawPaymentPassword()))
                .userKey(memberUpdate.getUserKey())
                .deleted(memberUpdate.isDeleted())
                .build();
    }

    public Member delete(){
        return Member.builder()
                .id(id)
                .loginId(loginId)
                .password(password)
                .memberProfile(memberProfile)
                .mainCardId(mainCardId)
                .paymentPassword(paymentPassword)
                .userKey(userKey)
                .deleted(true)
                .build();
    }

    public Member updatePaymentPassword(String paymentPassword, PasswordEncoder passwordEncoder){
        return Member.builder()
                .id(this.id)
                .loginId(this.loginId)
                .password(this.password)
                .memberProfile(this.memberProfile)
                .mainCardId(this.mainCardId)
                .paymentPassword(passwordEncoder.encode(paymentPassword))
                .userKey(this.userKey)
                .deleted(this.deleted)
                .build();
    }

    public boolean checkPaymentPassword(String rawPaymentPassword, PasswordEncoder passwordEncoder){
        return paymentPassword == passwordEncoder.encode(rawPaymentPassword);
    }

    public Member updateMainCardId(UUID mainCardId){
        return Member.builder()
                .id(this.id)
                .loginId(this.loginId)
                .password(this.password)
                .memberProfile(this.memberProfile)
                .mainCardId(mainCardId)
                .paymentPassword(this.paymentPassword)
                .userKey(this.userKey)
                .deleted(this.deleted)
                .build();
    }


    //찾을 수 없다면 -1 반환
    //todo maginNumber 처리 해줘야함
    public int findMainCardIdx(List<Card> cards) {
        for(int idx = 0, size = cards.size(); idx < size; idx++){
            if(cards.get(idx).getId().equals(mainCardId)){
                return idx;
            }
        }

        return -1;
    }
}

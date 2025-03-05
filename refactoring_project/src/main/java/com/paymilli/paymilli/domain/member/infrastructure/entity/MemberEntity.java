package com.paymilli.paymilli.domain.member.infrastructure.entity;

import com.paymilli.paymilli.domain.card.infrastructure.entity.CardEntity;
import com.paymilli.paymilli.domain.member.domain.Member;
import com.paymilli.paymilli.domain.member.domain.MemberProfile;
import com.paymilli.paymilli.domain.member.dto.request.AddMemberRequest;
import com.paymilli.paymilli.domain.member.dto.response.MemberInfoResponse;
import com.paymilli.paymilli.domain.payment.infrastructure.entity.PaymentEntity;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CardEntity> cardEntities = new ArrayList<CardEntity>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PaymentEntity> paymentEntities = new ArrayList<PaymentEntity>();

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private String paymentPassword;

    @Column(nullable = false)
    private String userKey;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    //    cardEntity 개발시 제거 예정
    @OneToOne
    @JoinColumn(name = "main_card_id")
    private CardEntity mainCardEntity;

    @Column
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @ColumnDefault("false")
    private boolean deleted;

    public static MemberEntity toEntity(AddMemberRequest addMemberRequest, String userKey,
                                        LocalDate birthday, String encodePassword, String encodePaymentPassword, String email) {
        return MemberEntity.builder()
            .loginId(addMemberRequest.getLoginId())
            .password(encodePassword)
            .name(addMemberRequest.getName())
            .birthday(birthday)
            .gender(addMemberRequest.getGender())
            .role(Role.USER)
            .paymentPassword(encodePaymentPassword)
            .userKey(userKey)
            .email(email)
            .phone(addMemberRequest.getPhone())
            .build();
    }


    public void setPaymentPassword(String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }

    public void setMainCardEntity(CardEntity mainCardEntity) {
        this.mainCardEntity = mainCardEntity;
    }

    public void delete() {
        deleted = true;
        mainCardEntity = null;
    }

    public void create() {
        deleted = false;
    }

    public MemberInfoResponse makeResponse() {
        return MemberInfoResponse.builder()
            .loginId(loginId)
            .name(name)
            .email(email)
            .gender(gender)
            .phone(phone)
            .build();
    }

    public void update(AddMemberRequest addMemberRequest, String encodePassword,
        String encodePaymentPassword, LocalDate birthday) {
        name = addMemberRequest.getName();
        password = encodePassword;
        this.birthday = birthday;
        gender = addMemberRequest.getGender();
        phone = addMemberRequest.getPhone();
        paymentPassword = encodePaymentPassword;
    }

    public Member toModel(){
        return Member.builder()
                .id(id)
                .loginId(loginId)
                .password(password)
                .memberProfile(MemberProfile.builder()
                        .name(name)
                        .birthday(birthday)
                        .gender(gender)
                        .role(role)
                        .email(email)
                        .phone(phone)
                        .build())
                .paymentPassword(paymentPassword)
                .userKey(userKey)
                .build();
    }
    public static MemberEntity fromModel(Member member, CardEntity cardEntity){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.id = member.getId();
        memberEntity.loginId = member.getLoginId();
        memberEntity.password = member.getPassword();

        MemberProfile memberProfile = member.getMemberProfile();
        memberEntity.name = memberProfile.getName();
        memberEntity.birthday = memberProfile.getBirthday();
        memberEntity.gender = memberProfile.getGender();
        memberEntity.role = memberProfile.getRole();
        memberEntity.email = memberProfile.getEmail();
        memberEntity.phone = memberProfile.getPhone();

        memberEntity.mainCardEntity = cardEntity;
        memberEntity.paymentPassword = member.getPaymentPassword();
        memberEntity.userKey = member.getUserKey();
        memberEntity.deleted = member.isDeleted();
        return memberEntity;
    }
}

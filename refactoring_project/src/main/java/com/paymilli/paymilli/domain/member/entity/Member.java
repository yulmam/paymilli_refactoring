package com.paymilli.paymilli.domain.member.entity;

import com.paymilli.paymilli.domain.card.entity.Card;
import com.paymilli.paymilli.domain.member.dto.request.AddMemberRequest;
import com.paymilli.paymilli.domain.member.dto.response.MemberInfoResponse;
import com.paymilli.paymilli.domain.payment.entity.PaymentGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
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
public class Member {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToMany(mappedBy = "member")
    private List<Card> cards = new ArrayList<Card>();

    @OneToMany(mappedBy = "member")
    private List<PaymentGroup> paymentGroups = new ArrayList<PaymentGroup>();

    @Column(nullable = false)
    private String memberId;

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

    //    card 개발시 제거 예정
    @OneToOne
    @JoinColumn(name = "main_card_id")
    private Card mainCard;

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

    public static Member toEntity(AddMemberRequest addMemberRequest, String userKey,
        LocalDate birthday, String encodePassword, String encodePaymentPassword, String email) {
        return Member.builder()
            .memberId(addMemberRequest.getMemberId())
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

    //    연관관계 편의 메서드
    public void addCard(Card card) {
        cards.add(card);
        card.setMember(this);
    }

    //    연관관계 편의 메서드
    public void addPaymentGroup(PaymentGroup paymentGroup) {
        paymentGroups.add(paymentGroup);
        paymentGroup.setMember(this);
    }

    public void setPaymentPassword(String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }

    public void setMainCard(Card mainCard) {
        this.mainCard = mainCard;
    }

    public void delete() {
        deleted = true;
        mainCard = null;
    }

    public void create() {
        deleted = false;
    }

    public MemberInfoResponse makeResponse() {
        return MemberInfoResponse.builder()
            .memberId(memberId)
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
}

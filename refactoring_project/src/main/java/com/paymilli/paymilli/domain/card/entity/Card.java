package com.paymilli.paymilli.domain.card.entity;


import com.paymilli.paymilli.domain.card.dto.client.CardValidationResponse;
import com.paymilli.paymilli.domain.card.dto.request.AddCardRequest;
import com.paymilli.paymilli.domain.card.dto.response.CardResponse;
import com.paymilli.paymilli.domain.member.entity.Member;
import com.paymilli.paymilli.domain.payment.entity.Payment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "card")
    private List<Payment> payments;

    @Column(nullable = false)
    private String cardNumber;

    @Column(nullable = false)
    private String CVC;

    @Column(nullable = false)
    private String expirationDate;

    @Column(nullable = false)
    private String cardName;

    @Column(nullable = false)
    private String cardHolderName;

    @Column(nullable = false)
    private String cardImage;

    @Column(nullable = false)
    private CardType cardType;

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

    @Builder
    public Card(Member member, String cardNumber, String CVC, String expirationDate,
        String cardName,
        String cardHolderName, String cardImage, CardType cardType) {
        this.member = member;
        this.cardNumber = cardNumber;
        this.CVC = CVC;
        this.expirationDate = expirationDate;
        this.cardName = cardName;
        this.cardHolderName = cardHolderName;
        this.cardImage = cardImage;
        this.cardType = cardType;
    }

    public static Card toEntity(AddCardRequest addCardRequest,
        CardValidationResponse cardValidationResponse, Member member) {
        return Card.builder()
            .cardNumber(addCardRequest.getCardNumber())
            .CVC(addCardRequest.getCvc())
            .expirationDate(addCardRequest.getExpirationDate())
            .cardHolderName(addCardRequest.getCardHolderName())
            .cardImage(cardValidationResponse.getCardImage())
            .cardName(cardValidationResponse.getCardName())
            .cardType(cardValidationResponse.getCardType())
            .member(member)
            .build();
    }

    public void delete() {
        deleted = true;
    }

    public void create() {
        deleted = false;
    }

    public CardResponse makeResponse() {
        return CardResponse.builder()
            .cardId(id)
            .cardName(cardName)
            .cardType(cardType)
            .cardLastNum(cardNumber.substring(12, 16))
            .cardImage(cardImage)
            .build();
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
        payment.setCard(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        Card card = (Card) o;
        return id != null && Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

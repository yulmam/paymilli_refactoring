package com.paymilli.paymilli.domain.card.infrastructure.entity;


import com.paymilli.paymilli.domain.card.domain.Card;
import com.paymilli.paymilli.domain.member.infrastructure.entity.MemberEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.Objects;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cardEntity")
public class CardEntity {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

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

    @ColumnDefault("false")
    private boolean deleted;
    @Column
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;



    @Builder
    public CardEntity(MemberEntity memberEntity, String cardNumber, String CVC, String expirationDate,
                      String cardName,
                      String cardHolderName, String cardImage, CardType cardType) {
        this.memberEntity = memberEntity;
        this.cardNumber = cardNumber;
        this.CVC = CVC;
        this.expirationDate = expirationDate;
        this.cardName = cardName;
        this.cardHolderName = cardHolderName;
        this.cardImage = cardImage;
        this.cardType = cardType;
    }

    public static CardEntity fromModel(Card card, MemberEntity memberEntity){
        CardEntity cardEntity = new CardEntity();
        cardEntity.id = card.getId();
        cardEntity.memberEntity = memberEntity;
        cardEntity.cardNumber = card.getCardInfo().getCardNumber();
        cardEntity.CVC = card.getCardInfo().getCvc();
        cardEntity.expirationDate = card.getCardInfo().getExpirationDate();
        cardEntity.cardName = card.getCardName();
        cardEntity.cardHolderName = card.getCardHolderName();
        cardEntity.cardImage = card.getCardImage();
        cardEntity.cardType = card.getCardType();
        return cardEntity;
    }

    public Card toModel(){
        return Card.builder()
                .id(id)
                .memberId(memberEntity.getId())
                .cardNumber(cardNumber)
                .cvc(CVC)
                .expirationDate(expirationDate)
                .cardName(cardName)
                .cardHolderName(cardHolderName)
                .cardImage(cardImage)
                .cardType(cardType)
                .build();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        CardEntity cardEntity = (CardEntity) o;
        return id != null && Objects.equals(id, cardEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

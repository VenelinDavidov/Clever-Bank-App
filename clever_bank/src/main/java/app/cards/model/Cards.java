package app.cards.model;

import app.customer.model.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cards {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Customer customer;

    @Column
    private String cardNumber;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardBrand cardBrand;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CardLevel cardLevel;

    private boolean isActive;

    @Column
    @Enumerated(EnumType.STRING)
    private CardPeriod period;

    @Column
    private int totalLimit;

    @Column
    private int amountUsed;

    @Column
    private int availableAmount;

    private boolean updateAllowed;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime completedOn;
}

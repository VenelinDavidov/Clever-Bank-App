package app.pocket.model;

import app.customer.model.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pockets")
public class Pocket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Customer customer;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PocketStatus status;

    @Column
    @Enumerated(EnumType.STRING)
    private PocketType type;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;


    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdOn = now;
        updatedOn = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedOn = LocalDateTime.now();
    }
}

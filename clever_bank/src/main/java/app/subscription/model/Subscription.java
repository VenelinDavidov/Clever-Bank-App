package app.subscription.model;

import app.customer.model.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Customer customer;

    @Column(nullable = false)
    private String serviceName;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPeriod period;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal charge;


    private boolean updateAllowed;




    @Column(nullable = false)
    private LocalDateTime completedOn;

    @Column(nullable = false)
    private LocalDateTime createdOn;
}

package app.customer.model;

import app.cards.model.Cards;
import app.subscription.model.Subscription;
import app.pocket.model.Pocket;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    private String address;

    private String profilePicture;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private boolean isActive;


    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;





    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    @OrderBy("createdOn DESC")
    private List <Cards> cards = new ArrayList <> ();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    @OrderBy("createdOn DESC")
    private List<Subscription> subscriptions = new ArrayList <> ();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    @OrderBy("createdOn DESC")
    private List<Pocket> wallets = new ArrayList <> ();
}

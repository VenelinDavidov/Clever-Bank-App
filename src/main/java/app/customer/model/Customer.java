package app.customer.model;

import app.bill.model.Bill;
import app.subscription.model.Subscription;
import app.wallet.model.Wallet;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


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
    private List <Bill> bills = new ArrayList <> ();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    @OrderBy("createdOn DESC")
    private List<Subscription> subscriptions = new ArrayList <> ();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    @OrderBy("createdOn ASC")
    private List<Wallet> wallets = new ArrayList <> ();
}

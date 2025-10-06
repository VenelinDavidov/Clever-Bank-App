package app.bills_utility.model;

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
@Builder@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bills")
public class Bill {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(unique = true, nullable = false)
    private String billNumber;

    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;


    @Enumerated(EnumType.STRING)
    @Column
    private BillStatus status;

    @Enumerated(EnumType.STRING)
    @Column
    private BillCategory category;

    @Column(nullable = false)
    private LocalDateTime createdOn;



    @ManyToOne
    private Customer customer;
}

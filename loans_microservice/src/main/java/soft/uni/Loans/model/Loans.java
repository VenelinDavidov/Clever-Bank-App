package soft.uni.Loans.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "loans")
@EntityListeners(AuditingEntityListener.class)
public class Loans   {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID loanId;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus loanStatus;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private BigDecimal interestRate;

    @Column
    private BigDecimal monthlyPayment;

    private Integer termMonths;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdOn;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedOn;


    @PrePersist
    public void prePersist(){
        if (loanStatus == null){
            loanStatus = LoanStatus.PENDING;
        }
        calculatedLoanDetails();
    }



    // calculate loan details for interest rate based on loan type
    private void calculatedLoanDetails(){
        if (interestRate == null){
            switch (loanType.toUpperCase ()){
                case "PERSONAL":
                    interestRate = new BigDecimal("8.5");
                    termMonths = 36;
                    break;
                case "MORTGAGE":
                    interestRate = new BigDecimal("3.5");
                    termMonths = 360;
                    break;
                case "BUSINESS":
                    interestRate = new BigDecimal("6.5");
                    termMonths = 60;
                    break;
                case "STUDENT":
                    interestRate = new BigDecimal("4.5");
                    termMonths = 120;
                    break;
                default:
                    interestRate = new BigDecimal("10.0");
                    termMonths = 24;
            }
        }

        // Calculate monthly payment
        if (amount != null && interestRate != null && termMonths != null){

            double monthlyRate = interestRate.doubleValue() / 100 / 12;
            double payment = amount.doubleValue() *
                    (monthlyRate * Math.pow(1 + monthlyRate, termMonths)) /
                    (Math.pow(1 + monthlyRate, termMonths) - 1);
            monthlyPayment = BigDecimal.valueOf(payment).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }

}

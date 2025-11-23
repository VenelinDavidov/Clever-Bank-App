package app.loans.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {

    private UUID loanId;
    private UUID customerId;
    private String firstName;
    private String lastName;
    private String loanType;
    private String loanStatus;
    private BigDecimal amount;

    private BigDecimal interestRate;
    private BigDecimal monthlyPayment;
    private Integer termMonths;

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}

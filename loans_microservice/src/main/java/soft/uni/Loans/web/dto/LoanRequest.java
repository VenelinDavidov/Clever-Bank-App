package soft.uni.Loans.web.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {


    private UUID customerId;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Loan type is required")
    private String loanType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "100.0", message = "Loan amount must be at least 100")
    private BigDecimal amount;


}

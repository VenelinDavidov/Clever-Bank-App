package app.loans.client.dto;


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

    private String firstName;

    private String lastName;

    private String loanType;

    private BigDecimal amount;



}

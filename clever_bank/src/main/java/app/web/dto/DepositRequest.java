package app.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;


@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositRequest {

    @NotNull
    @Size(min = 2, max = 25, message = "Invalid IBAN")
    private String iban;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @Size(min = 3, max = 3, message = "Invalid CVV")
    private String cvv;


    @Positive
    private BigDecimal amount;
}

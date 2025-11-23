package app.web.dto;

import app.bills_utility.model.BillCategory;
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
public class BillsRequest {


    @NotNull (message = "Bill number is required")
    @Size(min = 4, max = 10, message = "Bill number must be between 4 and 10 characters")
    private String billNumber;

    @Positive
    @NotNull (message = "Amount is required")
    private BigDecimal amount;


    private BillCategory billCategory;

    @Size(min = 2, max = 100, message = "Description must be between 2 and 100 characters")
    private String description;



}

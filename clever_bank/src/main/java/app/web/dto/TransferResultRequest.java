package app.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferResultRequest {

    @NotNull
    private UUID pocketId;

    @NotNull(message = "Username is required")
    private String username;

    @NotNull
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
}

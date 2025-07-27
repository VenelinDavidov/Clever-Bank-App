package app.web.dto;

import app.customer.model.Country;
import app.customer.model.Gender;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {


    @Size(min = 2, max = 20, message = "Username must between 2 and 20 character!")
    @NotEmpty (message = "Username is required")
    private String username;

    @Pattern (regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$",
              message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number")
    @NotEmpty (message = "Password is required")
    private String password;


    @NotEmpty(message = "Phone Number can not be a null or empty")
    @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile Number must be 10 digits")
    private String phoneNumber;


    @NotNull
    private Country country;

    @NotNull
    private Gender gender;
}

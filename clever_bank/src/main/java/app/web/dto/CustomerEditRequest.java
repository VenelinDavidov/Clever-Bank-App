package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
public class CustomerEditRequest {

    @NotBlank
    @Size( max = 30, message = "First name must be 30 character!")
    private String firstName;

    @NotBlank
    @Size( max = 30, message = "Last name must be 30 character!")
    private String lastName;

    @URL(message = "Please provide a valid URL")
    private String profilePicture;

    @Pattern(regexp="(^$|[0-9]{10})",message = "Mobile Number must be 10 digits")
    private String phoneNumber;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Size( max = 250, message = "Address must be 250 character!")
    private String address;



    public CustomerEditRequest() {

    }
}

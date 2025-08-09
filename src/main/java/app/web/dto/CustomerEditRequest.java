package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
public class CustomerEditRequest {

    @Size( max = 30, message = "First name must be 30 character!")
    private String firstName;

    @Size( max = 30, message = "Last name must be 30 character!")
    private String lastName;

    @URL(message = "Please provide a valid URL")
    private String profilePicture;

    @Email(message = "Please provide a valid email address")
    private String email;
}

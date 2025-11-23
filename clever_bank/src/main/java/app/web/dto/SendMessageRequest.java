package app.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {


    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 30, message = "Name must between 2 and 30 character!")
    private String  name;

    @Email(message = "Please provide a valid email address")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Subject is required")
    @Size(min = 2, max = 50, message = "Subject must between 2 and 50 character!")
    private String subject;

    @NotBlank(message = "Message is required")
    @Size( max = 300, message = "Message max be 300 character!")
    private String message;
}

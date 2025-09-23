package app.web.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @Size(min = 2, max = 20, message = "Username must between 2 and 20 character!")
    private String username;

    @Size(min = 2, max = 20, message = "Password must between 2 and 20 character!")
    private String password;
}

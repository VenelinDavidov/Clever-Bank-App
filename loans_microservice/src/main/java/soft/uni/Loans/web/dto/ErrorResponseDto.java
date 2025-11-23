package soft.uni.Loans.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDto {


    private int status;
    private String message;
    private String timestamp;

    public ErrorResponseDto(int status, String message, String timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}

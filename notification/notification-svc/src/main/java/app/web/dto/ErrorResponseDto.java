package app.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ErrorResponseDto {

    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;


    public ErrorResponseDto(int status, String message, String path, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }
}

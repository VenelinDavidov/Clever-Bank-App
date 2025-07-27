package app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DomainException extends RuntimeException {

    private final String massage;
    private final HttpStatus status;


    public DomainException(String massage, HttpStatus status) {
        super(massage);
        this.massage = massage;
        this.status = status;
    }
}

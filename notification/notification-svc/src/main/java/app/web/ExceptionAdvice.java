package app.web;

import app.web.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@Hidden
@ControllerAdvice
public class ExceptionAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleExceptionNotFound(Exception e){

        ErrorResponseDto responseDto = new ErrorResponseDto (404, "Resource not found", e.getMessage(), LocalDateTime.now ());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseDto);

    }
}

package soft.uni.Loans.web;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import soft.uni.Loans.web.dto.ErrorResponseDto;

import java.time.LocalDateTime;

@Hidden
@ControllerAdvice
public class ExceptionAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity <ErrorResponseDto> handleNoResourceFoundException(Exception ex) {

          ErrorResponseDto responseDto = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), ex.getMessage(), LocalDateTime.now().toString());

          return ResponseEntity
                  .status(HttpStatus.NOT_FOUND)
                  .body(responseDto);

    }

}


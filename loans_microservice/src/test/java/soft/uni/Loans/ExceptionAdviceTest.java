package soft.uni.Loans;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import soft.uni.Loans.web.ExceptionAdvice;
import soft.uni.Loans.web.dto.ErrorResponseDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExceptionAdviceTest {

    @Test
    void handleNoResourceFoundException_shouldReturnErrorResponseDto() {


        // Given
        ExceptionAdvice advice = new ExceptionAdvice ();
        String path = "/api/loans/999";
        NoResourceFoundException exception = new NoResourceFoundException (org.springframework.http.HttpMethod.GET, path);
        String expectedMessage = exception.getMessage ();

        // When
        ResponseEntity <ErrorResponseDto> response = advice.handleNoResourceFoundException (exception);

        // Then
        assertNotNull (response);
        assertEquals (HttpStatus.NOT_FOUND, response.getStatusCode ());

        ErrorResponseDto dto = response.getBody ();
        assertNotNull (dto);
        assertEquals (HttpStatus.NOT_FOUND.value (), dto.getStatus ());
        assertEquals (expectedMessage, dto.getMessage ());
        assertNotNull (dto.getTimestamp ());
    }
}

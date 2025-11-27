package soft.uni.Loans.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import soft.uni.Loans.model.LoanStatus;
import soft.uni.Loans.service.LoansService;
import soft.uni.Loans.web.dto.LoanRequest;
import soft.uni.Loans.web.dto.LoanResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoansController.class)
public class LoansControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoansService loansService;

    private UUID customerId;
    private UUID loanId;
    private LoanResponse sampleLoanResponse;
    private LoanRequest sampleLoanRequest;

    @BeforeEach
    void setUp() {

        customerId = UUID.randomUUID();
        loanId = UUID.randomUUID();

        sampleLoanResponse = LoanResponse.builder()
                .loanId(loanId)
                .customerId(customerId)
                .firstName("John")
                .lastName("Doe")
                .loanType("Personal")
                .amount(BigDecimal.valueOf(1000))
                .interestRate(BigDecimal.valueOf(5.12))
                .monthlyPayment(BigDecimal.valueOf(100))
                .loanStatus(LoanStatus.PENDING)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        sampleLoanRequest = LoanRequest.builder()
                .customerId(customerId)
                .firstName("John")
                .lastName("Doe")
                .loanType("Personal")
                .amount(BigDecimal.valueOf(1000))
                .build();
    }


    @Test
    void testGetLoansByCustomer() throws Exception {

        List<LoanResponse> loans = List.of(sampleLoanResponse);

        when(loansService.getLoansByCustomerId(customerId)).thenReturn(loans);

        mockMvc.perform(get("/api/v1/loans/customer/{customerId}", customerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(loans.size()))
                .andExpect(jsonPath("$[0].customerId").value(customerId.toString()));
    }



    @Test
    void testCreateLoan() throws Exception {

        when(loansService.createLoan(sampleLoanRequest)).thenReturn(sampleLoanResponse);

        mockMvc.perform(post("/api/v1/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleLoanRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loanId").value(loanId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));
    }


    @Test
    void testGetLoanById() throws Exception {

        when(loansService.getLoanById(loanId)).thenReturn(sampleLoanResponse);

        mockMvc.perform(get("/api/v1/loans/{loanId}", loanId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(loanId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()));
    }

    @Test
    void givenTestToUpdateLoan_whenUpdateLoan_thenReturnSuccess() throws Exception {

        when (loansService.updateLoan (loanId, sampleLoanRequest)).thenReturn(sampleLoanResponse);

        mockMvc.perform (put ("/api/v1/loans/{loanId}", loanId)
                        .contentType (MediaType.APPLICATION_JSON)
                        .content (objectMapper.writeValueAsString (sampleLoanRequest)))
                .andExpect (status().isOk())
                .andExpect (jsonPath ("$.loanId").value (loanId.toString()))
                .andExpect (jsonPath ("$.customerId").value (customerId.toString()));

    }


    @Test
    void giveTestToDeleteLoan_whenDeleteLoan_thenReturnSuccess() throws Exception {


        mockMvc.perform (delete ("/api/v1/loans/{loanId}", loanId)
                        .contentType (MediaType.APPLICATION_JSON))
                .andExpect (status().isNoContent ());

        verify (loansService, times (1)).deleteLoan (loanId);

    }



    @Test
    void givenTestToUpdateLoanStatus_whenUpdateLoanStatus_thenReturnSuccess() throws Exception {

        when (loansService.updateLoanStatus (loanId, LoanStatus.APPROVED)).thenReturn(sampleLoanResponse);

        mockMvc.perform (patch ("/api/v1/loans/{loanId}/status", loanId)
                        .param ("status", "APPROVED ")
                )
                .andExpect (status().isOk())
                .andExpect (jsonPath ("$.loanId").value (loanId.toString()))
                .andExpect (jsonPath ("$.customerId").value (customerId.toString()));
    }


}

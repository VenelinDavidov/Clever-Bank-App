package soft.uni.Loans.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soft.uni.Loans.model.LoanStatus;
import soft.uni.Loans.model.Loans;
import soft.uni.Loans.repository.LoansRepository;
import soft.uni.Loans.web.dto.LoanRequest;
import soft.uni.Loans.web.dto.LoanResponse;

import java.math.BigDecimal;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
public class LoanServiceUTest {

    @Mock
    private LoansRepository loanRepository;

    @InjectMocks
    private LoansService loansService;


    @Test
    void givenLoanRequest_whenInvokeCreateLoan_thenReturnLoanResponse() {

        // given
        LoanRequest  loanRequest = LoanRequest.builder()
                .customerId (UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .loanType("Personal")
                .amount(BigDecimal.valueOf (1000))
                .build ();

        Loans loan = Loans.builder()
                .customerId (loanRequest.getCustomerId())
                .firstName(loanRequest.getFirstName())
                .lastName(loanRequest.getLastName())
                .loanType(loanRequest.getLoanType())
                .amount(loanRequest.getAmount())
                .loanStatus (LoanStatus.PENDING)
                .build();

        Loans savedLoan = Loans.builder()
                .loanId(UUID.randomUUID())
                .customerId(loan.getCustomerId())
                .firstName(loan.getFirstName())
                .lastName(loan.getLastName())
                .loanType(loan.getLoanType())
                .amount(loan.getAmount())
                .loanStatus(LoanStatus.PENDING)
                .build();

        when (loanRepository.save (any (Loans.class))).thenReturn (savedLoan);

        // when
        LoanResponse response = loansService.createLoan (loanRequest);

        // then
        assertNotNull(response);
        assertEquals (savedLoan.getLoanId (), response.getLoanId ());
        assertEquals (loanRequest.getCustomerId (), response.getCustomerId ());
        assertEquals(LoanStatus.PENDING, response.getLoanStatus());
        assertEquals(loanRequest.getAmount(), response.getAmount());
        assertEquals(loanRequest.getFirstName(), response.getFirstName());
        assertEquals(loanRequest.getLastName(), response.getLastName());

    }
}

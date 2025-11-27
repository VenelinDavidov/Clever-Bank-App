package soft.uni.Loans.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import soft.uni.Loans.exception.ResourceNotFoundException;
import soft.uni.Loans.model.LoanStatus;
import soft.uni.Loans.model.Loans;
import soft.uni.Loans.repository.LoansRepository;
import soft.uni.Loans.web.dto.LoanRequest;
import soft.uni.Loans.web.dto.LoanResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanServiceUTest {

    @Mock
    private LoansRepository loanRepository;

    @InjectMocks
    private LoansService loansService;


    @Test
    void givenLoanRequest_whenInvokeCreateLoan_thenReturnLoanResponse() {

        // given
        LoanRequest loanRequest = LoanRequest.builder ()
                .customerId (UUID.randomUUID ())
                .firstName ("John")
                .lastName ("Doe")
                .loanType ("Personal")
                .amount (BigDecimal.valueOf (1000))
                .build ();

        Loans loan = Loans.builder ()
                .customerId (loanRequest.getCustomerId ())
                .firstName (loanRequest.getFirstName ())
                .lastName (loanRequest.getLastName ())
                .loanType (loanRequest.getLoanType ())
                .amount (loanRequest.getAmount ())
                .loanStatus (LoanStatus.PENDING)
                .build ();

        Loans savedLoan = Loans.builder ()
                .loanId (UUID.randomUUID ())
                .customerId (loan.getCustomerId ())
                .firstName (loan.getFirstName ())
                .lastName (loan.getLastName ())
                .loanType (loan.getLoanType ())
                .amount (loan.getAmount ())
                .loanStatus (LoanStatus.PENDING)
                .build ();

        when (loanRepository.save (any (Loans.class))).thenReturn (savedLoan);

        // when
        LoanResponse response = loansService.createLoan (loanRequest);

        // then
        assertNotNull (response);
        assertEquals (savedLoan.getLoanId (), response.getLoanId ());
        assertEquals (loanRequest.getCustomerId (), response.getCustomerId ());
        assertEquals (LoanStatus.PENDING, response.getLoanStatus ());
        assertEquals (loanRequest.getAmount (), response.getAmount ());
        assertEquals (loanRequest.getFirstName (), response.getFirstName ());
        assertEquals (loanRequest.getLastName (), response.getLastName ());

    }


    @Test
    void givenLoansByIdForCustomer_thenReturnLoanResponse() {

        UUID customerId = UUID.randomUUID ();

        Loans loans = Loans.builder ()
                .loanId (UUID.randomUUID ())
                .customerId (customerId)
                .firstName ("John")
                .lastName ("Doe")
                .loanType ("Personal")
                .amount (BigDecimal.valueOf (1000))
                .interestRate (BigDecimal.valueOf (5.12))
                .monthlyPayment (BigDecimal.valueOf (100))
                .loanStatus (LoanStatus.PENDING)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();

        when (loanRepository.findByCustomerId (customerId)).thenReturn (List.of (loans));


        List <LoanResponse> response = loansService.getLoansByCustomerId (customerId);

        assertNotNull (response);
        assertEquals (1, response.size ());

        LoanResponse resp = response.get (0);
        assertEquals (loans.getLoanId (), resp.getLoanId ());
        assertEquals (loans.getCustomerId (), resp.getCustomerId ());
        assertEquals (loans.getFirstName (), resp.getFirstName ());
        assertEquals (loans.getLastName (), resp.getLastName ());
        assertEquals (loans.getLoanType (), resp.getLoanType ());
        assertEquals (loans.getAmount (), resp.getAmount ());
        assertEquals (loans.getInterestRate (), resp.getInterestRate ());
        assertEquals (loans.getMonthlyPayment (), resp.getMonthlyPayment ());
        assertEquals (loans.getLoanStatus (), resp.getLoanStatus ());
        assertEquals (loans.getCreatedOn (), resp.getCreatedOn ());
        assertEquals (loans.getUpdatedOn (), resp.getUpdatedOn ());
    }

    @Test
    void givenLoanById_thenReturnLoanResponse() {

        UUID customerId = UUID.randomUUID ();
        UUID loanId = UUID.randomUUID ();

        Loans loans = Loans.builder ()
                .loanId (loanId)
                .customerId (customerId)
                .firstName ("John")
                .lastName ("Doe")
                .loanType ("Personal")
                .amount (BigDecimal.valueOf (1000))
                .interestRate (BigDecimal.valueOf (5.12))
                .monthlyPayment (BigDecimal.valueOf (100))
                .loanStatus (LoanStatus.PENDING)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();

        when (loanRepository.findById (loanId)).thenReturn (Optional.of (loans));

        LoanResponse response = loansService.getLoanById (loanId);

        assertNotNull (response);
        assertEquals (loans.getLoanId (), response.getLoanId ());
        assertEquals (loans.getCustomerId (), response.getCustomerId ());
        assertEquals (loans.getFirstName (), response.getFirstName ());
        assertEquals (loans.getLastName (), response.getLastName ());
        assertEquals (loans.getLoanType (), response.getLoanType ());
        assertEquals (loans.getAmount (), response.getAmount ());
        assertEquals (loans.getInterestRate (), response.getInterestRate ());
        assertEquals (loans.getMonthlyPayment (), response.getMonthlyPayment ());
        assertEquals (loans.getLoanStatus (), response.getLoanStatus ());
        assertEquals (loans.getCreatedOn (), response.getCreatedOn ());
        assertEquals (loans.getUpdatedOn (), response.getUpdatedOn ());
    }

    @Test
    void givenLoanId_whenNotFound_thenThrowException(){


        UUID loanId = UUID.randomUUID ();

        when (loanRepository.findById (loanId)).thenReturn (Optional.empty ());

        assertThrows (ResourceNotFoundException.class, () -> loansService.getLoanById (loanId));
    }


    @Test
    void givenUpdateLoan_whenInvokeUpdateLoan_thenReturnLoanResponse(){

        UUID customerId = UUID.randomUUID ();
        UUID loanId = UUID.randomUUID ();

        Loans existingLoan = Loans.builder ()
                .loanId (loanId)
                .customerId (customerId)
                .firstName ("John")
                .lastName ("Doe")
                .loanType ("Personal")
                .amount (BigDecimal.valueOf (1000))
                .interestRate (BigDecimal.valueOf (5.12))
                .monthlyPayment (BigDecimal.valueOf (100))
                .loanStatus (LoanStatus.PENDING)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();

        Loans updatedLoan = Loans.builder ()
                .loanId (loanId)
                .customerId (customerId)
                .firstName ("John")
                .lastName ("Doe")
                .loanType ("Personal")
                .amount (BigDecimal.valueOf (500))
                .interestRate (BigDecimal.valueOf (6))
                .monthlyPayment (BigDecimal.valueOf (150))
                .loanStatus (LoanStatus.PENDING)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();

        LoanRequest loanRequest = LoanRequest.builder ()
                .customerId (customerId)
                .firstName ("John")
                .lastName ("Doe")
                .loanType ("Personal")
                .amount (BigDecimal.valueOf (1000))
                .build ();

        when (loanRepository.findById (loanId)).thenReturn (Optional.of (existingLoan));
        when (loanRepository.save (any (Loans.class))).thenReturn (updatedLoan);

        //when
        LoanResponse response = loansService.updateLoan (loanId, loanRequest);
        // then
        assertNotNull (response);
        assertEquals (updatedLoan.getLoanId (), response.getLoanId ());
        assertEquals (updatedLoan.getCustomerId (), response.getCustomerId ());
        assertEquals (updatedLoan.getFirstName (), response.getFirstName ());
        assertEquals (updatedLoan.getLastName (), response.getLastName ());
        assertEquals (updatedLoan.getLoanType (), response.getLoanType ());
        assertEquals (updatedLoan.getAmount (), response.getAmount ());
        assertEquals (updatedLoan.getInterestRate (), response.getInterestRate ());
        assertEquals (updatedLoan.getMonthlyPayment (), response.getMonthlyPayment ());
        assertEquals (updatedLoan.getLoanStatus (), response.getLoanStatus ());
        assertEquals (updatedLoan.getCreatedOn (), response.getCreatedOn ());
        assertEquals (updatedLoan.getUpdatedOn (), response.getUpdatedOn ());

    }


    @Test
    void givenDeletedLoan_thenReturnDeletedLoan(){

        UUID loanId = UUID.randomUUID ();

        Loans loans = Loans.builder ()
                .loanId (loanId)
                .customerId (UUID.randomUUID ())
                .firstName ("John")
                .lastName ("Doe")
                .loanType ("Personal")
                .amount (BigDecimal.valueOf (1000))
                .interestRate (BigDecimal.valueOf (5.12))
                .monthlyPayment (BigDecimal.valueOf (100))
                .loanStatus (LoanStatus.PENDING)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();

        when (loanRepository.findById (loanId)).thenReturn (Optional.of (loans));

        loansService.deleteLoan (loanId);

        verify (loanRepository, times (1)).delete (loans);
    }

    @Test
    void givenUpdateLoanStatus_thenReturnUpdatedLoanStatus(){

        UUID loanId = UUID.randomUUID ();

        Loans loans = Loans.builder ()
                .loanId (loanId)
                .customerId (UUID.randomUUID ())
                .firstName ("John")
                .lastName ("Doe")
                .loanType ("Personal")
                .amount (BigDecimal.valueOf (1000))
                .interestRate (BigDecimal.valueOf (5.12))
                .monthlyPayment (BigDecimal.valueOf (100))
                .loanStatus (LoanStatus.PENDING)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();

        when (loanRepository.findById (loanId)).thenReturn (Optional.of (loans));
        when (loanRepository.save (loans)).thenReturn (loans);

        loansService.updateLoanStatus (loanId, LoanStatus.APPROVED);

        verify (loanRepository, times (1)).save (loans);
    }
}

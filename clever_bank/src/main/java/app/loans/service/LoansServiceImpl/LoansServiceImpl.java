package app.loans.service.LoansServiceImpl;



import app.loans.client.LoansClient;
import app.loans.client.dto.LoanRequest;
import app.loans.client.dto.LoanResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Slf4j
@Service
public class LoansServiceImpl {

    private final LoansClient loansClient;


    @Autowired
    public LoansServiceImpl(LoansClient loansClient) {
        this.loansClient = loansClient;
    }


    public List <LoanResponse> getLoansByCustomerId(UUID customerId) {

        log.info ("Fetch loan for customer: {}", customerId);
        try{
            return loansClient.getLoansByCustomerId (customerId);
        } catch (Exception e) {
            log.error ("Error creating loan for customer: {}", customerId, e);
            throw new RuntimeException ("Error creating loan for customer: " + e.getMessage ());
        }
    }



    public LoanResponse createLoan(@Valid LoanRequest loanRequest) {
      log.info ("Creating loan for customer: {}", loanRequest.getCustomerId ());

        return loansClient.createLoan(loanRequest);
    }



    public LoanResponse getLoan(UUID loanId) {
        log.info ("Fetching loan: {}", loanId);
        try {
          return  loansClient.getLoan (loanId);
          }catch (Exception e){
            log.error ("Error fetching loan: {}", loanId, e);
            throw new RuntimeException ("Error fetching loan: " + e.getMessage ());
        }
    }



    public LoanRequest builderLoan(LoanResponse loan) {
        return LoanRequest.builder()
                .customerId (loan.getCustomerId ())
                .firstName (loan.getFirstName ())
                .lastName (loan.getLastName ())
                .loanType (loan.getLoanType ())
                .amount (loan.getAmount ())
                .build();
    }



    public LoanResponse updateLoan(UUID loanId, LoanRequest loanRequest) {

        log.info ("Updating loan: {}", loanId);
        try {
          return  loansClient.updateLoan (loanId, loanRequest);
        }catch (Exception e){
             log.error ("Error updating loan: {}", loanId, e);
             throw new RuntimeException ("Error updating loan: " + e.getMessage ());
        }
    }


    public void  deleteLoan(UUID loanId) {

        log.info ("Deleting loan: {}", loanId);
        try {
            loansClient.deleteLoan (loanId);
        }catch (Exception e){
            log.error ("Error deleting loan: {}", loanId, e);
            throw new RuntimeException ("Error deleting loan: " + e.getMessage ());
        }
    }
}
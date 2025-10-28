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
}
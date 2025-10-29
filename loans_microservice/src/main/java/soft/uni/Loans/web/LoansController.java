package soft.uni.Loans.web;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import soft.uni.Loans.service.LoansService;
import soft.uni.Loans.web.dto.LoanRequest;
import soft.uni.Loans.web.dto.LoanResponse;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;


@Slf4j
@RestController
@RequestMapping("/api/v1/loans")
public class LoansController {

    private static final Logger logger = LoggerFactory.getLogger (LoansController.class);
    private final LoansService loansService;

    @Autowired
    public LoansController(LoansService loansService) {
        this.loansService = loansService;
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List <LoanResponse>> getLoansByCustomer(@PathVariable UUID customerId) {
        log.info("Received request to get loans for customer: {}", customerId);
        List<LoanResponse> responses = loansService.getLoansByCustomerId(customerId);
        return ResponseEntity.ok(responses);
    }


    @PostMapping
    public ResponseEntity <LoanResponse> createLoan (@Valid @RequestBody LoanRequest loanRequest){
       log.info ("Received request to create loan");
       LoanResponse response =  loansService.createLoan (loanRequest);
       return ResponseEntity
               .status (HttpStatus.CREATED)
               .body (response);
    }


    @GetMapping("/{loanId}")
    public ResponseEntity <LoanResponse> getLoan (@PathVariable UUID loanId){
        log.info ("Receive request to get loan: {}", loanId);
        LoanResponse response = loansService.getLoanById (loanId);
        return ResponseEntity
                .ok(response);
    }

   @PutMapping("/{loanId}")
   public ResponseEntity <LoanResponse> updateLoan(@PathVariable UUID loanId, @RequestBody LoanRequest loanRequest){

    log.info ("Receive request to update loan: {}", loanId);

    LoanResponse response = loansService.updateLoan (loanId,loanRequest);
    return ResponseEntity
            .ok(response);
   }


   @DeleteMapping("/{loanId}")
   public  ResponseEntity <Void> deleteLoan (@PathVariable UUID loanId){

       log.info ("Receive request to delete loan: {}", loanId);
       loansService.deleteLoan(loanId);
       return ResponseEntity
               .noContent()
               .build();
   }

}


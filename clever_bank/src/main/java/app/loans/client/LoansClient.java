package app.loans.client;

import app.loans.client.dto.LoanRequest;
import app.loans.client.dto.LoanResponse;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "loans-vs", url = "${loans-vs.base-url}")
public interface LoansClient {


      @GetMapping("/customer/{customerId}")
      List <LoanResponse> getLoansByCustomerId(@PathVariable UUID customerId);

      @GetMapping("/{loanId}")
      LoanResponse getLoan(@PathVariable UUID loanId);

      @PostMapping
      LoanResponse createLoan(@RequestBody LoanRequest loanRequest);

      @PutMapping("/{loanId}")
      LoanResponse updateLoan(@PathVariable UUID loanId, @RequestBody LoanRequest loanRequest);

      @DeleteMapping("/{loanId}")
      void deleteLoan(@PathVariable UUID loanId);

}

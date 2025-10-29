package app.web;



import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.loans.client.dto.LoanRequest;

import app.loans.client.dto.LoanResponse;
import app.loans.service.LoansServiceImpl.LoansServiceImpl;
import app.security.AuthenticationMetadataDetails;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/loans")
public class LoansController {

    private final LoansServiceImpl loansService;
    private final CustomerService customerService;

    @Autowired
    public LoansController(LoansServiceImpl loansService,
                           CustomerService customerService) {
        this.loansService = loansService;
        this.customerService = customerService;
    }




    // this is the main page for loans
    @GetMapping
    public ModelAndView showLoanForm(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails) {

        Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());

        ModelAndView modelAndView = new ModelAndView("loans");
        modelAndView.addObject("loanRequest", new LoanRequest());
        modelAndView.addObject ("customer", customer);

        try {
            List <LoanResponse> loansByCustomerId = loansService.getLoansByCustomerId (customer.getId ());
            modelAndView.addObject ("loans", loansByCustomerId);
        } catch (Exception e) {
            log.error ("Error getting loans for customer: {}", customer.getId (), e);
            modelAndView.addObject ("loans", List.of ());
        }
        return modelAndView;
    }




    // create loan
   @PostMapping
   public ModelAndView createLoan (@Valid LoanRequest loanRequest,
                                   @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails,
                                   RedirectAttributes redirectAttributes){

       Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());
       loanRequest.setCustomerId (customer.getId ());
       LoanResponse response = null;

        try {
            response = loansService.createLoan (loanRequest);
            redirectAttributes.addFlashAttribute ("successMessage", "Loan created successfully! Loan ID: " + response.getLoanId ());

        }catch (Exception e) {
           log.error ("Error creating loan: ", e);
           redirectAttributes.addFlashAttribute ("errorMessage", "Failed to create loan: " + e.getMessage ());
        }
       return new ModelAndView("redirect:/loans");
   }




   @GetMapping("/edit/{loanId}")
   public ModelAndView showEditForm(@PathVariable UUID loanId,
                                    @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails){

        Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());
        LoanResponse loan = loansService.getLoan (loanId);
        LoanRequest loanRequest = loansService.builderLoan (loan);

        ModelAndView modelAndView = new ModelAndView ("loans-edit");
        modelAndView.addObject ("customer", customer);
        modelAndView.addObject ("loan", loan);
        modelAndView.addObject ("loanRequest",loanRequest);

        return modelAndView;
   }




   @PostMapping("/update/{loanId}")
   public ModelAndView updateLoan (@PathVariable UUID loanId,
                                   @ModelAttribute LoanRequest loanRequest,
                                   @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails,
                                                            RedirectAttributes redirectAttributes){



       try {
           ModelAndView modelAndView = new ModelAndView ("loans-edit");
           Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());
           loanRequest.setCustomerId (customer.getId ());
           LoanResponse loanResponse = loansService.updateLoan (loanId, loanRequest);
           redirectAttributes.addFlashAttribute ("successMessage", "Loan update successfully!");
           return new ModelAndView("redirect:/loans");

       }catch (Exception e){
           log.error ("Error updating loan: ", e);
           redirectAttributes.addFlashAttribute ("errorMessage", "Failed to update loan: " + e.getMessage ());

           return new ModelAndView("redirect:/loans");
       }
   }






   @PostMapping("/delete/{loanId}")
   public String deleteLoan (@PathVariable UUID loanId,
                             @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails,
                             RedirectAttributes redirectAttributes){

        try {
            loansService.deleteLoan (loanId);
            redirectAttributes.addFlashAttribute ("successMessage", "Loan deleted successFully!");
        }catch (Exception e){
            log.error ("Error deleting loan: ", e);
           redirectAttributes.addFlashAttribute ("errorMessage", "Failed to delete loan: " + e.getMessage ());
        }

       return "redirect:/loans";
   }
}

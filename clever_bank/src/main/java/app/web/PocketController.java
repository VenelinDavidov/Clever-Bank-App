package app.web;

import app.customer.model.Customer;
import app.customer.service.CustomerService;

import app.pocket.service.PocketService;
import app.security.AuthenticationMetadataDetails;
import app.transaction.model.Transactions;
import app.web.dto.DepositRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/pockets")
public class PocketController {


    private final PocketService pocketService;
    private final CustomerService customerService;


    @Autowired
    public PocketController(PocketService pocketService,
                            CustomerService customerService) {
        this.pocketService = pocketService;
        this.customerService = customerService;
    }






    @GetMapping
    public ModelAndView getPocketPage(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails) {

        Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());
        Map <UUID, List<Transactions>> LastSevenTransactions = pocketService.getLastSevenTransactions(customer.getWallets ());

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.addObject ("customer", customer);
        modelAndView.addObject ("LastSevenTransactions", LastSevenTransactions);
        modelAndView.setViewName ("pockets");

        return modelAndView;
    }



  // Switch pocket status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchPocket(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails) {

        pocketService.switchStatusWallet (id, authenticationMetadataDetails.getCustomerId ());
        return "redirect:/pockets";
    }



    // Show deposit form
    @GetMapping("/{id}/deposit-form")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ModelAndView  showDepositForm(@PathVariable UUID id,
                                         @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails,
                                         DepositRequest depositRequest) {

        Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.addObject ("customer", customer);
        modelAndView.addObject ("depositRequest", depositRequest);
        modelAndView.addObject ("pocketId", id);
        modelAndView.setViewName ("deposit-form");

        return modelAndView;
    }



    // Deposit money implementation this method
    @PostMapping("/{pocketId}/deposit-form")
    public ModelAndView deposit(@PathVariable UUID pocketId,
                                @Valid DepositRequest depositRequest,
                                @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails,
                                BindingResult bindingResult) {

        if (bindingResult.hasErrors ()){

            ModelAndView modelAndView = new ModelAndView ();

            modelAndView.addObject ("pocketId", pocketId);
            modelAndView.addObject ("depositRequest", depositRequest);
            modelAndView.setViewName ("deposit-form");

            return modelAndView;
        }

        pocketService.deposit(pocketId, depositRequest, authenticationMetadataDetails.getCustomerId ());

        return new ModelAndView ("redirect:/pockets");
    }
}

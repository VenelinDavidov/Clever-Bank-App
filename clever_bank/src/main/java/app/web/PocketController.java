package app.web;

import app.customer.model.Customer;
import app.customer.service.CustomerService;

import app.pocket.service.PocketService;
import app.security.AuthenticationMetadataDetails;
import app.transaction.model.TransactionStatus;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        Map <UUID, List <Transactions>> lastSevenTransactions = pocketService.getLastSevenTransactions (customer.getWallets ());

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.setViewName ("pockets");
        modelAndView.addObject ("customer", customer);
        modelAndView.addObject ("lastSevenTransactions", lastSevenTransactions);

        return modelAndView;
    }




    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String switchPocket(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails) {

        pocketService.switchStatusWallet (id, authenticationMetadataDetails.getCustomerId ());
        return "redirect:/pockets";
    }




    @GetMapping("/{id}/deposit-form")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ModelAndView showDepositForm(@PathVariable UUID id,
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





    @PostMapping("/{pocketId}/deposit-form")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ModelAndView deposit(@PathVariable UUID pocketId,
                                @Valid @ModelAttribute DepositRequest depositRequest, BindingResult bindingResult,
                                @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors ()) {

            ModelAndView modelAndView = new ModelAndView ();
            modelAndView.addObject ("pocketId", pocketId);
            modelAndView.addObject ("depositRequest", depositRequest);
            modelAndView.setViewName ("deposit-form");
            return modelAndView;
        }

        Transactions result = pocketService.deposit (pocketId, depositRequest, authenticationMetadataDetails.getCustomerId ());

        if (result.getStatus () == TransactionStatus.SUCCEEDED) {
            redirectAttributes.addFlashAttribute ("success", "Deposit successful");
            return new ModelAndView ("redirect:/pockets");
        }
        redirectAttributes.addFlashAttribute ("error", "Deposit failed");
        return new ModelAndView ("redirect:/transactions");
    }

}

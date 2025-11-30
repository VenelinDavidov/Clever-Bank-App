package app.web;

import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.pocket.service.PocketService;

import app.security.AuthenticationMetadataDetails;
import app.transaction.model.Transactions;
import app.web.dto.TransferResultRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/transfers")
public class TransferController {


    private final PocketService pocketService;
    private final CustomerService customerService;



    @Autowired
    public TransferController(PocketService pocketService, CustomerService customerService) {
        this.pocketService = pocketService;
        this.customerService = customerService;
    }




    @GetMapping
    public ModelAndView getTransferPage(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails) {

        Customer customer = customerService.getById(authenticationMetadataDetails.getCustomerId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject ("customer", customer);
        modelAndView.addObject ("transferResultRequest", TransferResultRequest.builder().build());
        modelAndView.setViewName("transfers");

        return modelAndView;
    }





    @PostMapping
    private ModelAndView createTransfer(@Valid TransferResultRequest transferResultRequest,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails){

         Customer customer = customerService.getById(authenticationMetadataDetails.getCustomerId());


        if (bindingResult.hasErrors ()){
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("transfers");
            modelAndView.addObject ("transferResultRequest", transferResultRequest);
            modelAndView.addObject ("customer", customer);
            return modelAndView;
        }

        Transactions transactions = pocketService.transfer(transferResultRequest, customer);


        return new ModelAndView("redirect:/transactions/" + transactions.getId());


    }
}

package app.web;

import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.security.AuthenticationMetadataDetails;
import app.transaction.model.Transactions;
import app.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/transactions")
public class TransactionController {


    private final TransactionService transactionService;
    private final CustomerService customerService;


    @Autowired
    public TransactionController(TransactionService transactionService,
                                 CustomerService customerService) {
        this.transactionService = transactionService;
        this.customerService = customerService;
    }




    @GetMapping
    public ModelAndView getAllTransactionsPage(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails) {

        Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());
        List <Transactions> transactions = transactionService.getAllByCustomerId (authenticationMetadataDetails.getCustomerId ());

        ModelAndView modelAndView =new ModelAndView ();

        modelAndView.addObject ("transactions", transactions);
        modelAndView.addObject ("customer", customer);
        modelAndView.setViewName ("transactions");

        return modelAndView;

    }



    @GetMapping("/{id}")
    public ModelAndView  getByTransactionId (@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails) {

        Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());
        Transactions transactions = transactionService.getById (id);

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.addObject ("customer",customer);
        modelAndView.addObject ("transaction",transactions);
        modelAndView.setViewName ("transaction-result");

        return modelAndView;
    }

}

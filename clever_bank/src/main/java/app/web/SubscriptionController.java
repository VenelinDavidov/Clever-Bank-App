package app.web;

import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.security.AuthenticationMetadataDetails;
import app.subscription.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final CustomerService customerService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService,
                                  CustomerService customerService) {
        this.subscriptionService = subscriptionService;
        this.customerService = customerService;
    }


    @GetMapping
    public ModelAndView getSubscriptionPage(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataDetails) {

        Customer customer = customerService.getById (authenticationMetadataDetails.getCustomerId ());

        ModelAndView modelAndView = new ModelAndView ();

        modelAndView.addObject ("customer", customer);
        modelAndView.setViewName ("upgrade");

        return modelAndView;

    }
}

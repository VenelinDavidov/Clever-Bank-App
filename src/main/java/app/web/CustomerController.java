package app.web;

import app.customer.model.Customer;
import app.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    @GetMapping("/{id}/profile")
    public ModelAndView getProfileCustomer(@PathVariable UUID id) {

        Customer customer = customerService.getById(id);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile-customer");


        return modelAndView;
    }


    //Edit profile customer
    @PutMapping("/{id}/profile")
    public ModelAndView updateProfileCustomer(@PathVariable UUID id) {

        customerService.getById(id);

        ModelAndView modelAndView = new ModelAndView();



        return modelAndView;
    }
}


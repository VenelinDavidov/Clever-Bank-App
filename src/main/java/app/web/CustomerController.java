package app.web;

import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.security.AuthenticationMetadataDetails;
import app.web.dto.CustomerEditRequest;
import app.web.mapper.DTOMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;


    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }




    //Get all customers
    @GetMapping()
    public ModelAndView getAllCustomers(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataPr) {

        List <Customer> customers = customerService.getALLCustomers ();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("customers");
        modelAndView.addObject ("customers", customers);

        return modelAndView;
    }




    // Get profile for customer
    @GetMapping("/{id}/profile")
    public ModelAndView getProfileMenuCustomer(@PathVariable UUID id){

        Customer customer = customerService.getById (id);

        ModelAndView modelAndView = new ModelAndView ();

        modelAndView.addObject ("customer", customer);
        modelAndView.setViewName ("profile-customer");
        modelAndView.addObject ("customerEditRequest", DTOMapper.mapToCustomerEditRequest (customer));

        return modelAndView;
    }




    //Update profile for customer
    @PutMapping("/{id}/profile")
    public ModelAndView updateProfileCustomer(@PathVariable UUID id, @Valid CustomerEditRequest customerEditRequest, BindingResult bindingResult) {
        {

            if (bindingResult.hasErrors ()) {

                Customer customer = customerService.getById (id);
                ModelAndView modelAndView = new ModelAndView ();
                modelAndView.setViewName ("profile-customer");
                modelAndView.addObject ("customer", customer);
                modelAndView.addObject ("customerEditRequest", customerEditRequest);
                return modelAndView;
            }

            customerService.editCustomerDetails (id, customerEditRequest);

            return new ModelAndView ("redirect:/home");
        }
    }
}


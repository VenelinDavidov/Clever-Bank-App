package app.web;

import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.message.service.MessageService;
import app.security.AuthenticationMetadataDetails;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import app.web.dto.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    private final MessageService messageService;
    private final CustomerService customerService;

    @Autowired
    public IndexController(MessageService messageService,
                           CustomerService customerService) {
        this.messageService = messageService;
        this.customerService = customerService;
    }


    // index page
    @GetMapping("/")
    public String getIndexPage() {

        return "index";
    }



    //Login
    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String error) {

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.setViewName ("login");
        modelAndView.addObject ("loginRequest", new LoginRequest ());

        if (error != null) {
            modelAndView.addObject ("errorMessage", "Invalid username or password");
        }

        return modelAndView;
    }





    //Register
    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.setViewName ("register");
        modelAndView.addObject ("registerRequest", new RegisterRequest ());

        return modelAndView;
    }


    @PostMapping("/register")
    public  ModelAndView registerCustomer (@Valid RegisterRequest registerRequest, BindingResult bindingResult){

        if (bindingResult.hasErrors ()){
            return  new  ModelAndView ("register");
        }

         customerService.register (registerRequest);

        return new ModelAndView ("redirect:/login");
    }





    // Get home page and view all page
    @GetMapping("/home")
    public ModelAndView getHomePage (@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataPr) {

        Customer customer = customerService.getById (authenticationMetadataPr.getCustomerId ());

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.addObject ("customer", customer);
        modelAndView.setViewName ("home");

        return modelAndView;
    }




  // get About information for Clever Application
    @GetMapping("/about")
    public String getAboutUs() {

        return "about";
    }



   // Send Message for service app
    @GetMapping("/messages")
    public ModelAndView getMessageForUs() {

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.setViewName ("message");
        modelAndView.addObject ("sendMessageRequest", new SendMessageRequest ());

        return modelAndView;
    }



    @PostMapping("/messages")
    public String sendMassageForUs(@Valid SendMessageRequest sendMessageRequest, BindingResult bindingResult){

        if (bindingResult.hasErrors ()){
            return "message";
        }
       // Process the message to service
        messageService.saveMessageForService (sendMessageRequest);

        return "redirect:/thank-you";
    }




    @GetMapping("/thank-you")
    public String getMessageThankYouForUser(){

        return "thank-you";
    }
}

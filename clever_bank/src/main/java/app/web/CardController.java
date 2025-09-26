package app.web;

import app.cards.model.Cards;
import app.cards.service.CardService;
import app.customer.model.Customer;
import app.customer.service.CustomerService;
import app.exception.CardLimitExceededException;
import app.exception.DomainException;
import app.security.AuthenticationMetadataDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cards")
public class CardController {

    private static final String MESSAGE_FOR_COUNT_CARDS = "You have reached the maximum number of cards";

    private final CardService cardService;
    private final CustomerService customerService;


    @Autowired
    public CardController(CardService cardService,
                          CustomerService customerService) {
        this.cardService = cardService;
        this.customerService = customerService;
    }


    // Get cards page
    @GetMapping
    public ModelAndView fetchCardsPage(@AuthenticationPrincipal AuthenticationMetadataDetails authenticationMetadataPr) {

        Customer customer = customerService.getById (authenticationMetadataPr.getCustomerId ());
        List <Cards> cards = cardService.getAllCardsByCustomerId (authenticationMetadataPr.getCustomerId ());

        ModelAndView modelAndView = new ModelAndView ();
        modelAndView.addObject ("customer", customer);
        modelAndView.addObject ("cards", cards);
        modelAndView.setViewName ("cards");

        return modelAndView;
    }




    // Create card
    @PostMapping("/create")
    public ModelAndView createCard(@AuthenticationPrincipal
                                       AuthenticationMetadataDetails auth,
                                       RedirectAttributes redirectAttributes) {

        Customer customer = customerService.getById (auth.getCustomerId ());

        try {
            cardService.createSecondaryCard(customer);
        } catch (CardLimitExceededException e) {
            redirectAttributes.addFlashAttribute("errorMessage", MESSAGE_FOR_COUNT_CARDS);
        }

        return new ModelAndView("redirect:/cards");
    }





    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView deleteCard(@RequestParam UUID cardId) {

        cardService.deleteCard(cardId);
        return new ModelAndView("redirect:/cards");
    }




    @PutMapping("/block")
    public String switchStatusCard(@RequestParam UUID cardId) {

        cardService.switchStatusCard(cardId);

        return "redirect:/cards";
    }

}


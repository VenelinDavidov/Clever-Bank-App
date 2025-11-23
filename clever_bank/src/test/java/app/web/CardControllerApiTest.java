package app.web;

import app.cards.model.Cards;
import app.cards.service.CardService;
import app.customer.model.Customer;
import app.customer.model.UserRole;
import app.customer.repository.CustomerRepository;
import app.customer.service.CustomerService;
import app.security.AuthenticationMetadataDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@WebMvcTest(CardController.class)
public class CardControllerApiTest {

    @MockitoBean
    private  CardService cardService;
    @MockitoBean
    private  CustomerService customerService;
    @MockitoBean
    private  CustomerRepository customerRepository;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void givenRequestToCardsEndpoint_whenGetCards_thenReturnFetchCardsPage() throws Exception {

        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("Venko")
                .lastName("Davidov")
                .email("venko@abv.bg")
                .phoneNumber("0895121212")
                .role (UserRole.ADMIN)
                .profilePicture("https://example.com/profile.jpg")
                .address("Sofia")
                .build();

        List <Cards> cards = List.of (new Cards (), new Cards ());
        when (cardService.getAllCardsByCustomerId (customerId)).thenReturn (cards);
        when (customerService.getById (customerId)).thenReturn (customer);

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );


        MockHttpServletRequestBuilder request = get ("/cards")
                .with (user (authenticationMetadataDetails))
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().isOk ())
                .andExpect (model().attributeExists("cards"))
                .andExpect (model().attributeExists("customer"))
                .andExpect (view ().name ("cards"));

        verify (cardService, times (1)).getAllCardsByCustomerId (customerId);
        verify (customerService, times (1)).getById (customerId);
    }



    @Test
    void givenRequestToCreateCardForCustomer_whenCreateCard_thenReturnFetchCardsPage() throws Exception {

        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("Venko")
                .lastName("Davidov")
                .email("venko@abv.bg")
                .phoneNumber("0895121212")
                .role (UserRole.ADMIN)
                .profilePicture("https://example.com/profile.jpg")
                .address("Sofia")
                .build();

        when (customerService.getById (customerId)).thenReturn (customer);

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );



        MockHttpServletRequestBuilder request = post ("/cards/create")
                .with (user (authenticationMetadataDetails))
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().is3xxRedirection ())
                .andExpect (redirectedUrl ("/cards"));

        verify (customerService, times (1)).getById (customerId);
    }



    @Test
    void givenRequestFromCustomerToSwitchStatusCard_thenReturnFetchCardsPage() throws Exception {

        UUID customerId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("Venko")
                .lastName("Davidov")
                .email("venko@abv.bg")
                .phoneNumber("0895121212")
                .role (UserRole.ADMIN)
                .profilePicture("https://example.com/profile.jpg")
                .address("Sofia")
                .build();


        doNothing ().when (cardService).switchStatusCard (cardId);

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );

        MockHttpServletRequestBuilder request = put ("/cards/block")
                .param ("cardId", cardId.toString ())
                .with (user (authenticationMetadataDetails))
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().is3xxRedirection ())
                .andExpect (redirectedUrl ("/cards"));

        verify (cardService, times (1)).switchStatusCard (cardId);

    }


    @Test
    void givenRequestFromCustomerToDeleteCard_thenReturnFetchCardsPage() throws Exception {

        UUID customerId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("Venko")
                .lastName("Davidov")
                .email("venko@abv.bg")
                .phoneNumber("0895121212")
                .role (UserRole.ADMIN)
                .profilePicture("https://example.com/profile.jpg")
                .address("Sofia")
                .build();

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );

        when (customerService.getById (authenticationMetadataDetails.getCustomerId ())).thenReturn (customer);

        MockHttpServletRequestBuilder request = post ("/cards/delete")
                .param ("cardId", cardId.toString ())
                .with (user (authenticationMetadataDetails))
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().is3xxRedirection ())
                .andExpect (redirectedUrl ("/cards"));

        verify (cardService, times (1)).deleteCard (cardId);
    }



    @Test
    void givenRequestFromCustomerWhoNotAdmin_whenPostDeleteCard_thenReturnMessege() throws Exception {

        UUID customerId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();

        Customer customer = Customer.builder()
                .id(customerId)
                .firstName("Venko")
                .lastName("Davidov")
                .email("venko@abv.bg")
                .phoneNumber("0895121212")
                .role (UserRole.USER)
                .profilePicture("https://example.com/profile.jpg")
                .address("Sofia")
                .build();

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (
                customer.getId(),
                "Venko123",
                "Venelin7",
                UserRole.USER,
                true,
                LocalDateTime.now (),
                LocalDateTime.now ()
        );

        when(customerService.getById (authenticationMetadataDetails.getCustomerId ())).thenReturn (customer);

        MockHttpServletRequestBuilder request = post("/cards/delete")
                .param("cardId", cardId.toString())
                .with(user(authenticationMetadataDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages"))
                .andExpect (flash ().attribute ("errorMessage", "You are not allowed to delete a card"));

        verify (customerService, times (1)).getById (customerId);

    }
}

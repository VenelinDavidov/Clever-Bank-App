package app.web;

import app.cards.service.CardService;
import app.customer.model.Customer;
import app.customer.model.UserRole;
import app.customer.service.CustomerService;
import app.exception.CustomerAlreadyExistException;
import app.message.service.MessageService;
import app.security.AuthenticationMetadataDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static app.TestBuilder.aRandomCustomer;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(IndexController.class)
@AutoConfigureMockMvc(addFilters = false)
public class IndexControllerAPITest {

    //Web Layer API Test

    @MockitoBean
    private CustomerService customerService;
    @MockitoBean
    private MessageService messageService;
    @MockitoBean
    private CardService cardService;



    @Autowired
    private MockMvc mockMvc;



    @Test
    void givenRequestToIndexPage_thenReturnIndexPage() throws Exception {

        MockHttpServletRequestBuilder request = get ("/");
        mockMvc.perform (request)
                .andExpect (status ().isOk ())
                .andExpect (view ().name ("index"));
    }

    @Test
    void givenContactUsPage_thenReturnContactUsPage() throws Exception {

        MockHttpServletRequestBuilder request = get ("/contact-us");
        mockMvc.perform (request)
                .andExpect (status ().isOk ())
                .andExpect (view ().name ("contact-us"));
    }


    @Test
    void givenRequestToRegister_thenReturnRegisterPage() throws Exception {

        MockHttpServletRequestBuilder request = get ("/register");
        mockMvc.perform (request)
                .andExpect (status ().isOk ())
                .andExpect (view ().name ("register"))
                .andExpect (model ().attributeExists ("registerRequest"));
    }


    @Test
    void postRequestToRegister_thenRegisterHappyPath() throws Exception {

        MockHttpServletRequestBuilder request = post ("/register")
                .param ("username", "Venko123")
                .param ("password", "Venelin7")
                .param ("phoneNumber", "0895121212")
                .param ("country", "BULGARIA")
                .param ("gender", "MALE")
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().is3xxRedirection ())
                .andExpect (redirectedUrl ("/login"));
        verify (customerService, times (1)).register (any ());
    }


    @Test
    void givenRequestToRegisterEndpointWhenCustomerAlreadyExist_thenRedirectToRegisterWithFlashParameter() throws Exception {

        when (customerService.register (any ())).thenThrow (new CustomerAlreadyExistException ("Username already exist!"));

        MockHttpServletRequestBuilder request = post ("/register")
                .param ("username", "Venko123")
                .param ("password", "Venelin7")
                .param ("phoneNumber", "0895121212")
                .param ("country", "BULGARIA")
                .param ("gender", "MALE")
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().is3xxRedirection ())
                .andExpect (redirectedUrl ("/register"))
                .andExpect (flash ().attributeExists ("errorMessage"));
        verify (customerService, times (1)).register (any ());
    }


    @Test
    void givenRequestToRegisterEndpointWhenBindingResultHasErrors_thenRedirectToRegisterWithFlashParameter() throws Exception {

        MockHttpServletRequestBuilder request = post ("/register")
                .param ("username", "Venko123")
                .param ("password", "123123")
                .param ("phoneNumber", "0895121212")
                .param ("country", "BULGARIA")
                .param ("gender", "MALE")
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().isOk ())
                .andExpect (view ().name ("register"))
                .andExpect (model ().attributeHasFieldErrors ("registerRequest", "password"));
        verify (customerService, times (0)).register (any ());
    }


    @Test
    void givenRequestToLogin_thenReturnLoginPage() throws Exception {

        MockHttpServletRequestBuilder request = get ("/login");
        mockMvc.perform (request)
                .andExpect (status ().isOk ())
                .andExpect (view ().name ("login"))
                .andExpect (model ().attributeExists ("loginRequest"));
    }


    @Test
    void givenRequestToLoginEndpointWithErrorParameter_thenReturnLoginViewWithErrorMessage() throws Exception {

        MockHttpServletRequestBuilder request = get ("/login").param ("error", "");
        mockMvc.perform (request)
                .andExpect (status ().isOk ())
                .andExpect (view ().name ("login"))
                .andExpect (model ().attributeExists ("loginRequest", "errorMessage"));

    }

    @Test
    void givenAuthenticatedRequestToHome_thenReturnHomeView() throws Exception {

        UUID customerId = UUID.randomUUID();


        AuthenticationMetadataDetails auth = new AuthenticationMetadataDetails(
                customerId,
                "Venko123",
                "Venelin7",
                UserRole.USER,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );


        Customer mockCustomer = aRandomCustomer ();
        when(customerService.getById(customerId)).thenReturn(mockCustomer);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken (
                        auth,
                        null,
                        List.of(new SimpleGrantedAuthority ("ROLE_USER"))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        MockHttpServletRequestBuilder request = get("/home");


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("home"));


        verify(customerService, times(1)).getById(customerId);
    }


    @Test
    void givenRequestToAboutInformation_thenReturnAboutInformationView() throws Exception {

        MockHttpServletRequestBuilder request = get("/about");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("about"));
    }


    @Test
    void givenRequestToMessageForUs_thenReturnMessageForUsView() throws Exception {

        MockHttpServletRequestBuilder request = get("/messages");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("message"))
                .andExpect(model().attributeExists("sendMessageRequest"));
    }


   @Test
    void givenRequestToMessagesForUsEndpoint_whenSendMessage_thenBindingResultHasErrors() throws Exception {

        MockHttpServletRequestBuilder request = post("/messages")
                .param("name", "Venko")
                .param("email", "venko@abv.bg")
                .param("message", "")
                .param("subject", "Please Help me")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("message"))
                .andExpect(model().attributeHasFieldErrors("sendMessageRequest", "message"));
    }


    @Test
    void givenRequestToMessagesForUsEndpoint_whenSendMessage_thenSaveMessage() throws Exception {

        MockHttpServletRequestBuilder request = post("/messages")
                .param("name", "Venko")
                .param("email", "venko@abv.bg")
                .param("message", "Hello")
                .param("subject", "Please Help me")
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/thank-you"));
        verify(messageService, times(1)).saveMessageForService(any());
    }


    @Test
    void givenRequestToThankYou_thenReturnThankYouView() throws Exception {

        MockHttpServletRequestBuilder request = get("/thank-you");
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("thank-you"));
    }

}

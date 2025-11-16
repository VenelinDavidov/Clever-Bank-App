package app.web;


import app.customer.model.Customer;
import app.customer.model.UserRole;
import app.customer.service.CustomerService;
import app.security.AuthenticationMetadataDetails;
import app.web.dto.CustomerEditRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerApiTest {


    @MockitoBean
    private CustomerService customerService;


    @Autowired
    private MockMvc mockMvc;


    @Test
    void putUnauthorizedRequestToSwitchRole_thenReturn404NotFound() throws Exception {

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails (UUID.randomUUID (), "Venko123", "Venelin7", UserRole.USER, true, LocalDateTime.now (), LocalDateTime.now ());

        MockHttpServletRequestBuilder request = put ("/customers/{id}/role", UUID.randomUUID ())
                .with (user (authenticationMetadataDetails))
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().isNotFound ())
                .andExpect (view ().name ("not-found"));
    }

    @Test
    void putAuthorizedRequestToSwitchRole_thenRedirectToCustomerDetails() throws Exception {
        AuthenticationMetadataDetails authenticationMetadataDetails =
                new AuthenticationMetadataDetails(
                        UUID.randomUUID(), "Venko123", "Venelin7",
                        UserRole.ADMIN, true,
                        LocalDateTime.now(), LocalDateTime.now()
                );

        MockHttpServletRequestBuilder request = put("/customers/{id}/role", UUID.randomUUID())
                .with(user(authenticationMetadataDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));

        verify(customerService, times(1)).switchCustomerRole(any());
    }


    @Test
    void putAuthorizedRequestToSwitchStatus_thenRedirectToCustomerDetails() throws Exception {

        AuthenticationMetadataDetails authenticationMetadataDetails =
                new AuthenticationMetadataDetails(
                        UUID.randomUUID(), "Venko123", "Venelin7",
                        UserRole.ADMIN, true,
                        LocalDateTime.now(), LocalDateTime.now()
                );

        MockHttpServletRequestBuilder request = put("/customers/{id}/status", UUID.randomUUID())
                .with(user(authenticationMetadataDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers"));

        verify(customerService, times(1)).switchCustomerStatus(any());
    }


    @Test
    void putToAuthenticatedCustomer_thenUpdateProfileCustomer_thenRedirectToHomePage() throws Exception {

        UUID customerId = UUID.randomUUID();

        AuthenticationMetadataDetails authenticationMetadataDetails = new AuthenticationMetadataDetails(
                        UUID.randomUUID(), "Venko123", "Venelin7",
                        UserRole.ADMIN, true,
                        LocalDateTime.now(), LocalDateTime.now()
                );

        CustomerEditRequest editRequest = new CustomerEditRequest();
        editRequest.setFirstName("Venko");
        editRequest.setLastName("Vasilov");
        editRequest.setPhoneNumber("0895121212");
        editRequest.setEmail("venko@abv.bg");
        editRequest.setProfilePicture("https://example.com/profile.jpg");
        editRequest.setAddress("Sofia");

        doNothing().when(customerService).editCustomerDetails(any(UUID.class), any(CustomerEditRequest.class));

        MockHttpServletRequestBuilder request = put ("/customers/{id}/profile", customerId)
                .with (user (authenticationMetadataDetails))
                .with (csrf ())
                        .param ("firstName", editRequest.getFirstName ())
                                .param ("lastName", editRequest.getLastName ())
                                        .param ("phoneNumber", editRequest.getPhoneNumber ())
                                                .param ("email", editRequest.getEmail ())
                                                        .param ("profilePicture", editRequest.getProfilePicture ())
                                                                .param ("address", editRequest.getAddress ());

        mockMvc.perform (request)
                .andExpect (status ().is3xxRedirection ())
                .andExpect (redirectedUrl ("/home"));

        verify(customerService, times(1)).editCustomerDetails(eq(customerId), any(CustomerEditRequest.class));
    }


    @Test
    void putToCustomerRequest_whenUpdateProfileCustomer_thenBindingResultHasErrors() throws Exception {

        UUID customerId = UUID.randomUUID();

        Customer existingCustomer = new Customer();
        existingCustomer.setUsername ("Venko123");
        existingCustomer.setFirstName("Venko");
        existingCustomer.setLastName("Davidov");
        existingCustomer.setPhoneNumber("0895121212");
        existingCustomer.setEmail("venko@abv.bg");
        existingCustomer.setProfilePicture("https://example.com/profile.jpg");
        existingCustomer.setAddress("Sofia");
        existingCustomer.setRole(UserRole.USER);

        when(customerService.getById(customerId)).thenReturn(existingCustomer);


        CustomerEditRequest updateCustomer = new CustomerEditRequest();
        updateCustomer.setFirstName("");
        updateCustomer.setLastName("");
        updateCustomer.setPhoneNumber("");
        updateCustomer.setEmail("");
        updateCustomer.setProfilePicture("");
        updateCustomer.setAddress("");

        MockHttpServletRequestBuilder request = put("/customers/{id}/profile", customerId)
                .param("firstName", "")
                .param("lastName", "")
                .param("phoneNumber", "")
                .param("email", "")
                .param("profilePicture", "")
                .param("address", "")
                .with(csrf())
                .with(user("Venko123").roles("USER"));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("profile-customer"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attributeExists("customerEditRequest"));


        verify(customerService, never()).editCustomerDetails(any(UUID.class), any(CustomerEditRequest.class));
    }


    @Test
    void givenAuthorization_whenGetProfileMenuCustomer_thenReturnProfileCustomer() throws Exception {

        UUID customerId = UUID.randomUUID();

       Customer customer = new Customer();
       customer.setFirstName("Venko");
       customer.setLastName("Davidov");
       customer.setPhoneNumber("0895121212");
       customer.setEmail("venko@abv.bg");
       customer.setProfilePicture("https://example.com/profile.jpg");
       customer.setAddress("Sofia");
       customer.setRole(UserRole.USER);

       when (customerService.getById (customerId)).thenReturn (customer);

        mockMvc.perform(get("/customers/{id}/profile", customerId)
                        .with(user("Venko123").roles("ADMIN")) //
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-customer"))
                .andExpect(model().attributeExists("customer"))
                .andExpect(model().attributeExists("customerEditRequest"));

        verify(customerService, times(1)).getById(customerId);
    }

    @Test
    void givenAuthorizationForAllCustomers_whenInvokeGetAllCustomers_thenReturnAllCustomers() throws Exception {

        UUID customerId = UUID.randomUUID();
        AuthenticationMetadataDetails principal = new AuthenticationMetadataDetails(
                customerId, "Venko123", "Venelin7", UserRole.ADMIN, true,
                LocalDateTime.now(), LocalDateTime.now()
        );


        mockMvc.perform(get("/customers").with (user (principal))
                        .with(user("Venko123").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("customers"))
                .andExpect(model().attributeExists("customers"));

        verify(customerService, times(1)).getALLCustomers();
    }
}
package app.web;

import app.customer.model.Customer;
import app.customer.model.UserRole;
import app.customer.service.CustomerService;
import app.loans.client.dto.LoanRequest;
import app.loans.client.dto.LoanResponse;
import app.loans.service.LoansServiceImpl.LoansServiceImpl;
import app.security.AuthenticationMetadataDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoansController.class)
public class LoansControllerApiTest {


    @MockitoBean
    private LoansServiceImpl loansService;
    @MockitoBean
    private CustomerService customerService;


    @Autowired
    private MockMvc mockMvc;


    @Test
    void givenRequestToLoansEndpoint_thenReturnLoansPage() throws Exception {

        UUID customerId = UUID.randomUUID ();

        AuthenticationMetadataDetails principal = new AuthenticationMetadataDetails (
                customerId, "Venko123", "Venelin7", UserRole.ADMIN, true,
                LocalDateTime.now (), LocalDateTime.now ()
        );

        Customer customer = new Customer ();
        customer.setId (customerId);
        customer.setFirstName ("Venko");
        customer.setRole (UserRole.ADMIN);

        when (customerService.getById (customerId)).thenReturn (customer);
        when (loansService.getLoansByCustomerId (customerId)).thenReturn (Collections.emptyList ());


        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken (principal, null,
                        List.of (new SimpleGrantedAuthority ("ROLE_ADMIN")));

        mockMvc.perform (get ("/loans")
                        .with (authentication (auth)))
                .andExpect (status ().isOk ())
                .andExpect (view ().name ("loans"))
                .andExpect (model ().attributeExists ("loanRequest"))
                .andExpect (model ().attribute ("customer", customer))
                .andExpect (model ().attribute ("loans", Collections.emptyList ()));

    }


    @Test
    void givenAuthenticatedRequestToLoansCreateEndpoint_thenCreateLoan() throws Exception {

        UUID customerId = UUID.randomUUID();
        AuthenticationMetadataDetails principal = new AuthenticationMetadataDetails(
                customerId, "Venko123", "Venelin7", UserRole.ADMIN, true,
                LocalDateTime.now(), LocalDateTime.now()
        );

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setFirstName("Venko");
        customer.setRole(UserRole.ADMIN);

        LoanResponse loanResponse = new LoanResponse();
        loanResponse.setLoanId(UUID.randomUUID());

        when(customerService.getById(customerId)).thenReturn(customer);
        when(loansService.createLoan(any(LoanRequest.class))).thenReturn(loanResponse);

        mockMvc.perform(post("/loans")
                        .with(authentication(new UsernamePasswordAuthenticationToken(
                                principal, null, List.of(() -> "ROLE_ADMIN")
                        )))
                        .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/loans"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}

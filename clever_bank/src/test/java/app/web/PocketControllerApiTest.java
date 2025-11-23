package app.web;


import app.customer.model.Customer;
import app.customer.model.UserRole;
import app.customer.service.CustomerService;
import app.pocket.service.PocketService;
import app.security.AuthenticationMetadataDetails;
import app.transaction.model.TransactionStatus;
import app.transaction.model.Transactions;
import app.web.dto.DepositRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PocketController.class)
public class PocketControllerApiTest {

    @MockitoBean
    private  PocketService pocketService;
    @MockitoBean
    private  CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenRequestToPocketPage_thenReturnHappyPath() throws Exception {

        UUID customerId = UUID.randomUUID();

        AuthenticationMetadataDetails authDetails = new AuthenticationMetadataDetails (
                customerId,
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

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

        List <Transactions> expectedTransaction = List.of (new Transactions (), new Transactions (), new Transactions ());
        Map <UUID, List <Transactions>> lastSevenTransactions = Map.of (UUID.randomUUID (), expectedTransaction);

        when(customerService.getById(customerId)).thenReturn(customer);
        when (pocketService.getLastSevenTransactions (customer.getWallets ())).thenReturn (lastSevenTransactions);


        MockHttpServletRequestBuilder request = get("/pockets")
                .with(user(authDetails))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is2xxSuccessful ())
                .andExpect(view().name("pockets"))
                .andExpect (model().attributeExists("customer", "lastSevenTransactions"))
                .andExpect(model().attribute("customer", customer))
                .andExpect(model().attributeExists ("lastSevenTransactions"));

    }

    @Test
    void givenRequestToPocketStatus_whenPushButton_thenSwitchPocketStatus() throws Exception {

        UUID customerId = UUID.randomUUID();
        UUID pocketId = UUID.randomUUID();

        AuthenticationMetadataDetails authDetails = new AuthenticationMetadataDetails (
                customerId,
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        doNothing ().when (pocketService).switchStatusWallet (pocketId, customerId);

        MockHttpServletRequestBuilder request = put ("/pockets/{id}/status", pocketId)
                .with (csrf ())
                .with (user (authDetails));

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection ())
                .andExpect(redirectedUrl("/pockets"));
        verify (pocketService, times (1)).switchStatusWallet (pocketId, customerId);
    }



    @Test
    void givenRequestToDepositForm_whenGetRequest_thenReturnDepositForm() throws Exception {

        UUID customerId = UUID.randomUUID();
        UUID pocketId = UUID.randomUUID();

        AuthenticationMetadataDetails authDetails = new AuthenticationMetadataDetails (
                customerId,
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

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

        when (customerService.getById (authDetails.getCustomerId ())).thenReturn (customer);

        MockHttpServletRequestBuilder request = get ("/pockets/{id}/deposit-form", pocketId)
                .with (user (authDetails))
                .with (csrf ());

        mockMvc.perform (request)
                .andExpect (status ().is2xxSuccessful ())
                .andExpect (view ().name ("deposit-form"))
                .andExpect (model ().attributeExists ("customer", "depositRequest", "pocketId"))
                .andExpect (model ().attribute ("customer", customer))
                .andExpect (model ().attribute ("depositRequest", instanceOf (DepositRequest.class)))
                .andExpect (model ().attribute ("pocketId", pocketId));

    }

    @Test
    void givenRequestToPostDepositForPocket_whenInvokeDepositMethod_thenDepositPocketSuccessfully() throws Exception {

        UUID customerId = UUID.randomUUID();
        UUID pocketId = UUID.randomUUID();

        AuthenticationMetadataDetails authDetails = new AuthenticationMetadataDetails (
                customerId,
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        DepositRequest depositRequest = DepositRequest.builder()
                .iban("BG18STSA93000000001234")
                .firstName("Ivan")
                .lastName("Petrov")
                .cvv("123")
                .amount(BigDecimal.valueOf(100))
                .build();


        Transactions mockTransactions = new Transactions ();
        mockTransactions.setStatus (TransactionStatus.SUCCEEDED);

        when (pocketService.deposit (pocketId, depositRequest, authDetails.getCustomerId ())).thenReturn (mockTransactions);

        MockHttpServletRequestBuilder request = post ("/pockets/{id}/deposit-form", pocketId)
                .with (user (authDetails))
                .with (csrf ())
                .flashAttr ("depositRequest", depositRequest);

        mockMvc.perform (request)
                .andExpect (status ().is3xxRedirection ())
                .andExpect (redirectedUrl ("/pockets"));

        verify (pocketService, times (1)).deposit (pocketId, depositRequest, customerId);
    }

}

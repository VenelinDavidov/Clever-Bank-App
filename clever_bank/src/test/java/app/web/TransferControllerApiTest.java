package app.web;

import app.customer.model.Customer;
import app.customer.model.UserRole;
import app.customer.service.CustomerService;
import app.pocket.service.PocketService;
import app.security.AuthenticationMetadataDetails;
import app.web.dto.TransferResultRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
public class TransferControllerApiTest {

    @MockitoBean
    private  PocketService pocketService;
    @MockitoBean
    private  CustomerService customerService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void givenRequestToTransferPage_shouldReturnTransferPage() throws Exception {

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

        AuthenticationMetadataDetails authDetails = new AuthenticationMetadataDetails (
                customerId,
                "Venko123",
                "Venelin7",
                UserRole.ADMIN,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        TransferResultRequest transferResultRequest = TransferResultRequest.builder()
                .pocketId (UUID.randomUUID ())
                .username ("Venko123")
                .amount (BigDecimal.valueOf (100))
                .build();

        when(customerService.getById (authDetails.getCustomerId ())).thenReturn (customer);

        MockHttpServletRequestBuilder request = get ("/transfers")
                .with (user(authDetails))
                .with (csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("transfers"))
                .andExpect (model ().attributeExists ("customer"))
                .andExpect(model().attributeExists("transferResultRequest"))
                .andExpect(model().attribute("transferResultRequest", new TransferResultRequest()));

        verify (customerService,times (1)).getById (customer.getId ());

    }
}

package app.web;

import app.customer.model.Customer;
import app.customer.model.UserRole;
import app.customer.service.CustomerService;
import app.notification.client.dto.NotificationPreferenceResponse;
import app.notification.client.dto.NotificationResponse;
import app.notification.service.NotificationService;
import app.security.AuthenticationMetadataDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerApiTest {

    @MockitoBean
    private  CustomerService customerService;
    @MockitoBean
    private  NotificationService notificationService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void givenRequestToNotificationsEndpoint_whenGetNotificationsPage_thenReturnNotifications() throws Exception {


        UUID customerId = UUID.randomUUID();

        AuthenticationMetadataDetails authDetails = new AuthenticationMetadataDetails (
                customerId,
                "Venko123",
                "Venelin7",
                UserRole.USER,
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
                .role(UserRole.USER)
                .build();

        when (customerService.getById (customerId)).thenReturn (customer);

        NotificationPreferenceResponse preference = mock(NotificationPreferenceResponse.class);
        when(notificationService.getNotificationPreference(customerId)).thenReturn(preference);

        NotificationResponse notif1 = mock(NotificationResponse.class);
        when(notif1.getStatus()).thenReturn("SUCCEEDED");
        NotificationResponse notif2 = mock(NotificationResponse.class);
        when(notif2.getStatus()).thenReturn("SUCCEEDED");
        NotificationResponse notif3 = mock(NotificationResponse.class);
        when(notif3.getStatus()).thenReturn("FAILED");

        List <NotificationResponse> history = List.of (notif1, notif2, notif3);
        when (notificationService.getNotificationHistory (customerId)).thenReturn (history);

        MockHttpServletRequestBuilder request = get("/notifications")
                .with(user(authDetails))
                .with(csrf());


        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("notifications"))
                .andExpect(model().attribute("customer", customer))
                .andExpect(model().attribute("notificationPreference", preference))
                .andExpect(model().attribute("succeededNotificationsNumber", 2L))
                .andExpect(model().attribute("failedNotificationsNumber", 1L))
                .andExpect (model ().attribute ("notificationHistory", history.stream()
                                                                                        .limit (5)
                                                                                        .toList ()));


        verify (customerService, times (1)).getById (customerId);
        verify (notificationService, times (1)).getNotificationPreference (customerId);
        verify (notificationService, times (1)).getNotificationHistory (customerId);

    }
}

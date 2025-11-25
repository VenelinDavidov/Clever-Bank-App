package app.web;



import app.TestBuilder;
import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationType;
import app.repository.NotificationPreferenceRepository;
import app.repository.NotificationRepository;
import app.service.NotificationService;
import app.web.dto.NotificationPreferenceResponse;
import app.web.dto.NotificationRequest;
import app.web.dto.NotificationTypeRequest;
import app.web.dto.UpsertNotificationPreferenceRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static app.TestBuilder.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerApiTest {

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private MockMvc mockMvc;




    @Test
    void giveRequestNotificationPreference_HappyPath() throws Exception {

        when (notificationService.getNotificationPreferenceByUserId (any ())).thenReturn (aRandomNotificationPreference ());
        MockHttpServletRequestBuilder requestBuilder = get ("/api/v2/notifications/preferences").param ("customerId", UUID.randomUUID ().toString ());

        mockMvc.perform (requestBuilder)
                .andExpect (status ().isOk ())
                .andExpect (jsonPath ("id").isNotEmpty ())
                .andExpect (jsonPath ("customerId").isNotEmpty ())
                .andExpect (jsonPath ("enabled").isNotEmpty ())
                .andExpect (jsonPath ("contactInfo").isNotEmpty ())
                .andExpect (jsonPath ("type").isNotEmpty ());

    }


    @Test
    void postWithBodyToCreatePreference_return201CreatedAndCorrectDtoStructures() throws Exception {

        UpsertNotificationPreferenceRequest requestDto = UpsertNotificationPreferenceRequest.builder ()
                .customerId (UUID.randomUUID ())
                .notificationEnabled (true)
                .contactInfo ("contactInfo")
                .type (NotificationTypeRequest.EMAIL)
                .build ();

        when (notificationService.upsertNotificationPreference (any ())).thenReturn (aRandomNotificationPreference ());
        MockHttpServletRequestBuilder requestBuilder = post ("/api/v2/notifications/preferences")
                .contentType (MediaType.APPLICATION_JSON)
                .content (new ObjectMapper ().writeValueAsBytes (requestDto));


        mockMvc.perform (requestBuilder)
                .andExpect (status ().isCreated ())
                .andExpect (jsonPath ("id").isNotEmpty ())
                .andExpect (jsonPath ("customerId").isNotEmpty ())
                .andExpect (jsonPath ("enabled").isNotEmpty ())
                .andExpect (jsonPath ("contactInfo").isNotEmpty ())
                .andExpect (jsonPath ("type").isNotEmpty ());
    }


    @Test
    void givenRequestToSendNotificationFromCustomer_thenReturn201CreatedAndCorrectDtoStructures() throws Exception {

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .customerId (UUID.randomUUID ())
                .subject ("subject")
                .body ("body")
                .build ();

        when (notificationService.sendNotification (notificationRequest)).thenReturn (aRandomNotification ());

        MockHttpServletRequestBuilder requestBuilder = post ("/api/v2/notifications")
                .contentType (MediaType.APPLICATION_JSON)
                .content (new ObjectMapper ().writeValueAsBytes (notificationRequest));

        mockMvc.perform (requestBuilder)
                .andExpect (status ().isCreated ())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("subject").isNotEmpty())
                .andExpect(jsonPath("status").isNotEmpty())
                .andExpect(jsonPath("type").isNotEmpty())
                .andExpect(jsonPath("createdOn").isNotEmpty());

        verify (notificationService,times (1)).sendNotification (notificationRequest);
    }



    @Test
    void givenRequestToNotificationHistory_whenInvokeGetNotificationHistory_thenReturnHistoryAndCorrectDtoStructures() throws Exception {

        UUID customerId = UUID.randomUUID();

        Notification notification1 = aRandomNotification ();
        Notification notification2 = aRandomNotification();

        when (notificationService.getNotificationHistory (customerId)).thenReturn (List.of (notification1, notification2));

        MockHttpServletRequestBuilder requestBuilder = get ("/api/v2/notifications")
                .param ("customerId", customerId.toString())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform (requestBuilder)
                .andExpect (status ().isOk ());

        verify (notificationService, times (1)).getNotificationHistory (customerId);

    }

}
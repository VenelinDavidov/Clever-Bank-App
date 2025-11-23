package app.web;



import app.model.NotificationPreference;
import app.model.NotificationType;
import app.repository.NotificationPreferenceRepository;
import app.repository.NotificationRepository;
import app.service.NotificationService;
import app.web.dto.NotificationPreferenceResponse;
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

}
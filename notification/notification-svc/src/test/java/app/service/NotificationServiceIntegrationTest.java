package app.service;

import app.model.NotificationPreference;
import app.model.NotificationType;
import app.repository.NotificationPreferenceRepository;
import app.web.dto.NotificationTypeRequest;
import app.web.dto.UpsertNotificationPreferenceRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NotificationServiceIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationPreferenceRepository preferenceRepository;



    @Test
    void upsertNotificationPreference_whenPreferenceExists_shouldUpdateAndReturnUpdatedPreference() throws Exception {

        UUID customerId = UUID.randomUUID();

        // GIVEN
        NotificationPreference existingPreference = NotificationPreference.builder()
                .customerId(customerId)
                .contactInfo("oldContactInfo")
                .enabled(false)
                .type(NotificationType.EMAIL)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        preferenceRepository.save (existingPreference);

        // When
        UpsertNotificationPreferenceRequest updatePreference = UpsertNotificationPreferenceRequest.builder()
                .customerId (customerId)
                .notificationEnabled (true)
                .contactInfo ("contactInfo")
                .type (NotificationTypeRequest.EMAIL)
                .build();

        NotificationPreference updated = notificationService.upsertNotificationPreference (updatePreference);

        assertThat (updated.getId ()).isEqualTo (existingPreference.getId ());
        assertThat(updated.getContactInfo ()).isEqualTo("contactInfo");
        assertThat(updated.isEnabled ()).isTrue();
        assertThat(updated.getType ()).isEqualTo(NotificationType.EMAIL);


        NotificationPreference inDB = preferenceRepository.findByCustomerId (customerId).get ();
        assertThat (inDB.getId ()).isEqualTo(existingPreference.getId ());
        assertThat (inDB.getContactInfo ()).isEqualTo("contactInfo");
        assertThat(inDB.isEnabled()).isTrue();


    }

}

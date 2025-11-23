package app.web;

import app.model.NotificationPreference;
import app.repository.NotificationPreferenceRepository;
import app.service.NotificationService;

import app.web.dto.NotificationTypeRequest;
import app.web.dto.UpsertNotificationPreferenceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CreateNewPreferenceITest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationPreferenceRepository notificationPreferenceRepository;



    @BeforeEach
    void setup() {
        notificationPreferenceRepository.deleteAll();
    }




    @Test
    void createNotificationPreference_SuccessPath() {

        UUID customerId = UUID.randomUUID ();

        UpsertNotificationPreferenceRequest request = UpsertNotificationPreferenceRequest.builder ()
                .customerId (customerId)
                .notificationEnabled (true)
                .contactInfo ("test@email.com")
                .type (NotificationTypeRequest.EMAIL)
                .build ();

        notificationService.upsertNotificationPreference (request);

        List <NotificationPreference> allPreferences = notificationPreferenceRepository.findAll ();
        assertThat (allPreferences).hasSize (1);
        NotificationPreference preference = allPreferences.get (0);
        assertEquals (customerId, preference.getCustomerId ());
    }

}

package app;


import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationStatus;
import app.model.NotificationType;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

import java.util.UUID;

@UtilityClass
public class TestBuilder {




    public static NotificationPreference aRandomNotificationPreference() {

        return NotificationPreference.builder ()
                .id (UUID.randomUUID ())
                .customerId (UUID.randomUUID ())
                .enabled (true)
                .contactInfo ("contactInfo")
                .type (NotificationType.EMAIL)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build ();
    }


    public static Notification aRandomNotification() {

        return Notification.builder()
                .id (UUID.randomUUID ())
                .customerId (UUID.randomUUID ())
                .subject ("subject")
                .body ("body")
                .status (NotificationStatus.SUCCEEDED)
                .type (NotificationType.EMAIL)
                .deleted (false)
                .createdOn (LocalDateTime.now ())
                .build ();
    }

}

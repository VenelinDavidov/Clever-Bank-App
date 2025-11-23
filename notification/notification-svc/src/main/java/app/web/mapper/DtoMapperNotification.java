package app.web.mapper;


import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationType;
import app.web.dto.NotificationPreferenceResponse;
import app.web.dto.NotificationResponse;
import app.web.dto.NotificationTypeRequest;
import lombok.experimental.UtilityClass;



@UtilityClass
public class DtoMapperNotification {



    public static NotificationType fromNotificationTypeRequest(NotificationTypeRequest dto) {
        return switch (dto) {
            case EMAIL -> NotificationType.EMAIL;
        };
    }



     // Entity to Dto
    public static NotificationPreferenceResponse fromNotificationPreferenceToNotificationPreferenceResponse(NotificationPreference entity) {

         return NotificationPreferenceResponse.builder ()
                 .id (entity.getId ())
                 .customerId (entity.getCustomerId ())
                 .type (entity.getType ())
                 .enabled (entity.isEnabled ())
                 .contactInfo (entity.getContactInfo ())
                 .build ();
    }


    public static NotificationResponse fromNotificationToNotificationResponse(Notification notification) {

        return NotificationResponse.builder()
                .subject (notification.getSubject ())
                .status (notification.getStatus ())
                .type (notification.getType ())
                .createdOn (notification.getCreatedOn ())
                .build ();
    }
}

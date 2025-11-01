package app.service;

import app.model.Notification;
import app.model.NotificationPreference;
import app.model.NotificationStatus;
import app.model.NotificationType;
import app.repository.NotificationPreferenceRepository;
import app.repository.NotificationRepository;
import app.web.dto.NotificationRequest;
import app.web.dto.UpsertNotificationPreferenceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final MailSender mailSender;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               NotificationPreferenceRepository preferenceRepository,
                               MailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.mailSender = mailSender;
    }


    public NotificationPreference upsertNotificationPreference(UpsertNotificationPreferenceRequest upsertNotificationPreference) {

        log.info ("Upserting notification preference for user: {}", upsertNotificationPreference.getCustomerId ());
        Optional <NotificationPreference> optionalUserNotificationPreference =
                         preferenceRepository
                        .findByCustomerId (upsertNotificationPreference.getCustomerId ());

        if (optionalUserNotificationPreference.isPresent ()){
            NotificationPreference preference = optionalUserNotificationPreference.get ();
            preference.setContactInfo (upsertNotificationPreference.getContactInfo ());
            preference.setEnabled (upsertNotificationPreference.isNotificationEnabled ());
            preference.setType (NotificationType.EMAIL);
            preference.setUpdatedOn (LocalDateTime.now ());

            log.info ("Notification preference updated for user: {}", upsertNotificationPreference.getCustomerId ());
            return preferenceRepository.save (preference);
        }

        NotificationPreference preference = NotificationPreference.builder()
                .customerId (upsertNotificationPreference.getCustomerId ())
                .contactInfo (upsertNotificationPreference.getContactInfo ())
                .enabled (upsertNotificationPreference.isNotificationEnabled ())
                .type (NotificationType.EMAIL)
                .createdOn (LocalDateTime.now ())
                .updatedOn (LocalDateTime.now ())
                .build();

        log.info ("Notification preference saved for user: {}", upsertNotificationPreference.getCustomerId ());
        return preferenceRepository.save (preference);
    }




    public NotificationPreference getNotificationPreferenceByUserId(UUID customerId) {
        log.info ("Fetching notification preference for user: {}", customerId);

      return  preferenceRepository
              .findByCustomerId (customerId)
              .orElseThrow (() ->new NullPointerException("Notification preference for user id %s was not found.".formatted (customerId)));
    }




    public Notification sendNotification(NotificationRequest notificationRequest) {

        UUID customerId = notificationRequest.getCustomerId ();
        NotificationPreference customerPreference = getNotificationPreferenceByUserId (customerId);

        if (!customerPreference.isEnabled ()){
            throw new IllegalArgumentException("Notification is disabled for user: " + customerId);
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("venelin.davidov@gmail.com");
        message.setTo(customerPreference.getContactInfo());
        message.setSubject(notificationRequest.getSubject());
        message.setText(notificationRequest.getBody());

        Notification notification = Notification.builder()
                .subject (notificationRequest.getSubject ())
                .body (notificationRequest.getBody ())
                .createdOn (LocalDateTime.now ())
                .type (NotificationType.EMAIL)
                .customerId (customerId)
                .deleted (false)
                .build();



        try{
            mailSender.send (message);
            notification.setStatus (NotificationStatus.SUCCEEDED);
        } catch (Exception e){
            notification.setStatus (NotificationStatus.FAILED);
            log.warn ("There was an issue sending an email to %s due to %s.".formatted(customerPreference.getContactInfo(), e.getMessage()));
        }

        return notificationRepository.save (notification);
    }
}

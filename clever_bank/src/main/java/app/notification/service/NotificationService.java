package app.notification.service;


import app.notification.client.NotificationClient;
import app.notification.client.dto.NotificationPreferenceResponse;
import app.notification.client.dto.NotificationRequest;
import app.notification.client.dto.NotificationResponse;
import app.notification.client.dto.UpsertNotificationPreferenceRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class NotificationService {


    private final NotificationClient notificationClient;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @Value("${notification-service.clear-history-failed-message}")
    private String clearHistoryFailedMessage;




    public void saveNotificationPreference(UUID customerId, boolean isEmailEnabled, String email){

        UpsertNotificationPreferenceRequest  notificationPreference  = UpsertNotificationPreferenceRequest.builder()
                .customerId(customerId)
                .contactInfo (email)
                .type ("EMAIL")
                .notificationEnabled (isEmailEnabled)
                .build();


        try{
            ResponseEntity <Void> httpResponse = notificationClient.upsertNotificationPreference (notificationPreference);
            if (!httpResponse.getStatusCode ().is2xxSuccessful ()){
                log.error ("[Feign call to notification-ms failed] Can't save user preference for user with id = [%s]".formatted (customerId));
            }
        }catch (Exception exception){
           log.error ("Unable to call notifications service!");
        }
    }




    public NotificationPreferenceResponse getNotificationPreference(UUID customerId) {

        ResponseEntity <NotificationPreferenceResponse> customerPreference = notificationClient.getCustomerPreference (customerId);
        log.info ("Notification preference for customer: {}", customerId);

        if (!customerPreference.getStatusCode ().is2xxSuccessful ()){
            throw new RuntimeException ("Failed to get customer preference");

        }

        return customerPreference.getBody ();
    }




    public List <NotificationResponse> getNotificationHistory(UUID customerId) {
        ResponseEntity <List <NotificationResponse>> notificationHistory = notificationClient.getNotificationHistory (customerId);
        log.info ("Notification history for customer: {}", customerId);
        return notificationHistory.getBody ();
    }




    public void updateNotificationPreference(UUID customerId, boolean enabled) {

        try {
            notificationClient.updateNotificationPreference(customerId, enabled);
        } catch (Exception e){
            log.error ("Can't update notification preferences for user with id = [%s].".formatted(customerId));
        }
    }



    // send notification to customer
    public void sendNotification (UUID customerId, String subject, String body){

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .customerId(customerId)
                .subject(subject)
                .body(body)
                .build();

        try {
            ResponseEntity <Void> httpResponse = notificationClient.sendNotification (notificationRequest);
            if (!httpResponse.getStatusCode ().is2xxSuccessful ()){
                log.error ("[Feign call to notification-ms failed] Can't send notification for user with id = [%s].".formatted (customerId));
            }
        } catch (Exception e){
            log.error ("Can't send notification for user with id = [%s].".formatted(customerId));
        }
    }
}

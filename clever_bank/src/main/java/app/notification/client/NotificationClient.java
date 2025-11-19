package app.notification.client;

import app.notification.client.dto.NotificationPreferenceResponse;
import app.notification.client.dto.NotificationRequest;
import app.notification.client.dto.NotificationResponse;
import app.notification.client.dto.UpsertNotificationPreferenceRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "notification-ms", url = "${notification-ms.base-url}")
public interface NotificationClient {

    @PostMapping("/preferences")
    ResponseEntity<Void> upsertNotificationPreference(@RequestBody UpsertNotificationPreferenceRequest notificationPreference);

    @GetMapping("/preferences")
    ResponseEntity < NotificationPreferenceResponse> getCustomerPreference(@RequestParam(name = "customerId") UUID customerId) ;

    @GetMapping
    ResponseEntity  <List <NotificationResponse>> getNotificationHistory(@RequestParam(name = "customerId") UUID customerId);


    @PutMapping("/preferences")
    ResponseEntity<Void> updateNotificationPreference(@RequestParam(name="customerId") UUID customerId, @RequestParam(name="enabled") boolean enabled);


    @PostMapping
    ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest notificationRequest);


    @DeleteMapping
    ResponseEntity<Void> clearNotificationHistory(@RequestParam(name = "customerId") UUID customerId);


    @PutMapping
    ResponseEntity<Void> retryFailedNotifications(@RequestParam(name="customerId") UUID customerId);
}

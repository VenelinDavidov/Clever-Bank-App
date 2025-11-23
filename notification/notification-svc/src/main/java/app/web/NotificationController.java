package app.web;

import app.model.Notification;
import app.model.NotificationPreference;
import app.service.NotificationService;
import app.web.dto.NotificationPreferenceResponse;
import app.web.dto.NotificationRequest;
import app.web.dto.NotificationResponse;
import app.web.dto.UpsertNotificationPreferenceRequest;
import app.web.mapper.DtoMapperNotification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/notifications")
@Tag(name = "Notification", description = "Operations related to notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @Operation(summary = "Create or update notification preference", description = "Create or update notification preference")
    @PostMapping("/preferences")
    public ResponseEntity <NotificationPreferenceResponse> upsertNotificationPreference (@RequestBody UpsertNotificationPreferenceRequest upsertNotificationPreference){

        NotificationPreference preference = notificationService.upsertNotificationPreference (upsertNotificationPreference);

        NotificationPreferenceResponse dto = DtoMapperNotification.fromNotificationPreferenceToNotificationPreferenceResponse (preference);

        return ResponseEntity
                .status (HttpStatus.CREATED)
                .body (dto);
    }


    @GetMapping("/preferences")
    public ResponseEntity <NotificationPreferenceResponse> getCustomerNotificationPreference (@RequestParam(name = "customerId") UUID customerId){

        NotificationPreference notificationPreferenceByUserId = notificationService.getNotificationPreferenceByUserId (customerId);

        NotificationPreferenceResponse dto = DtoMapperNotification.fromNotificationPreferenceToNotificationPreferenceResponse (notificationPreferenceByUserId);

        return ResponseEntity
                .status (HttpStatus.OK)
                .body (dto);
    }


    @PostMapping
    public ResponseEntity<NotificationResponse> sendNotification (@RequestBody NotificationRequest notificationRequest){

        Notification notification = notificationService.sendNotification (notificationRequest);

        NotificationResponse dto = DtoMapperNotification.fromNotificationToNotificationResponse (notification);

        return ResponseEntity
                .status (HttpStatus.CREATED)
                .body (dto);
    }


    @GetMapping
    public ResponseEntity <List <NotificationResponse>> getNotificationHistory (@RequestParam (name = "customerId") UUID customerId){

        List<NotificationResponse> notificationHistory = notificationService.getNotificationHistory(customerId)
                                                                             .stream()
                                                                             .map(DtoMapperNotification::fromNotificationToNotificationResponse)
                                                                             .sorted(Comparator.comparing(NotificationResponse::getCreatedOn).reversed())
                                                                             .collect(Collectors.toList());

        return ResponseEntity
                .status (HttpStatus.OK)
                .body (notificationHistory);
    }





    @PutMapping("/preferences")
    public ResponseEntity <NotificationPreferenceResponse> changeNotification( @RequestParam (name = "customerId") UUID customerId,
                                                                               @RequestParam(name = "enabled") boolean enabled) {

        NotificationPreference preference = notificationService.changeNotificationPreference(customerId, enabled);

        NotificationPreferenceResponse dto = DtoMapperNotification.fromNotificationPreferenceToNotificationPreferenceResponse(preference);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }




    @DeleteMapping
    public ResponseEntity<Void> clearNotificationHistory(@RequestParam(name="customerId") UUID customerId){

        notificationService.clearNotifications(customerId);

        return ResponseEntity.ok().body(null);
    }



    @PutMapping
    public ResponseEntity <Void> retryNotificationFailed(@RequestParam (name="customerId") UUID customerId){

        notificationService.retryNotificationsFailed(customerId);

        return ResponseEntity.ok().body(null);
    }
}

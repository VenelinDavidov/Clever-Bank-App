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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
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
}

package app.notification.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UpsertNotificationPreferenceRequest {

    private UUID customerId;

    private boolean notificationEnabled;

    private String type;

    private String contactInfo;
}

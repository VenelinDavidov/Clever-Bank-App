package app.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class UpsertNotificationPreferenceRequest {

    @NotNull
    private UUID customerId;

    private boolean notificationEnabled;

    @NotNull
    private NotificationTypeRequest type;


    private String contactInfo;
}

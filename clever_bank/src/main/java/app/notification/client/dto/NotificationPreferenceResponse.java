package app.notification.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class NotificationPreferenceResponse {

    private String type;

    private boolean enabled;

    private String contactInfo;
}

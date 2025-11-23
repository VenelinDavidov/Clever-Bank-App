package app.notification.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class NotificationResponse {

    private String subject;

    private LocalDateTime createdOn;

    private String status;

    private String type;
}

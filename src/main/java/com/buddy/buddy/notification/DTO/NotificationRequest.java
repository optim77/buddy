package com.buddy.buddy.notification.DTO;

import com.buddy.buddy.notification.NotificationType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {
    private String consumerUsername;
    private UUID consumerId;
    private String broadcasterUsername;
    private UUID broadcasterId;
    private NotificationType type;
    private String message;
    private LocalDateTime createdAt;
}

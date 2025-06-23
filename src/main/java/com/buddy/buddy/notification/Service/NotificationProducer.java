package com.buddy.buddy.notification.Service;

import com.buddy.buddy.notification.DTO.LogoutNotificationRequest;
import com.buddy.buddy.notification.DTO.RegisterNotificationRequest;
import com.buddy.buddy.notification.NotificationType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public interface NotificationProducer {
    void registerNotification(RegisterNotificationRequest notificationRequest);
    void logoutNotification(LogoutNotificationRequest notificationRequest);
    void sendNotification(String consumerUsername,
                          UUID consumerId,
                          String broadcasterUsername,
                          UUID broadcasterId,
                          NotificationType type,
                          String message,
                          LocalDateTime createdAt);
}

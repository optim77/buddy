package com.buddy.buddy.notification.Service;

import com.buddy.buddy.notification.DTO.LogoutNotificationRequest;
import com.buddy.buddy.notification.DTO.NotificationRequest;
import com.buddy.buddy.notification.DTO.RegisterNotificationRequest;
import com.buddy.buddy.notification.NotificationType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificationProducerService implements NotificationProducer{

    private final WebClient webClient;

    public NotificationProducerService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void registerNotification(RegisterNotificationRequest notificationRequest) {
        webClient.post()
                .uri("/notification/register")
                .bodyValue(notificationRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.err.println("Failed to send notification: " + e.getMessage()))
                .subscribe();
    }

    @Override
    public void logoutNotification(LogoutNotificationRequest notificationRequest) {
        webClient.post()
                .uri("/notification/logout")
                .bodyValue(notificationRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.err.println("Failed to send notification: " + e.getMessage()))
                .subscribe();
    }

    public void prepareSend(NotificationRequest event) {
        webClient.post()
                .uri("/notifications")
                .bodyValue(event)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.err.println("Failed to send notification: " + e.getMessage()))
                .subscribe();
    }

    @Override
    public void sendNotification(
            String consumerUsername,
            UUID consumerId,
            String broadcasterUsername,
            UUID broadcasterId,
            NotificationType type,
            String message,
            LocalDateTime createdAt
            ) {
        NotificationRequest notificationRequest = new NotificationRequest(consumerUsername, consumerId, broadcasterUsername, broadcasterId, type, message, createdAt);
        prepareSend(notificationRequest);

    }
}

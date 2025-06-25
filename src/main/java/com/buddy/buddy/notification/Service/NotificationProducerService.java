package com.buddy.buddy.notification.Service;

import com.buddy.buddy.notification.DTO.LogoutNotificationRequest;
import com.buddy.buddy.notification.DTO.NotificationRequest;
import com.buddy.buddy.notification.DTO.RegisterNotificationRequest;
import com.buddy.buddy.notification.NotificationType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

    @Value("${notification-service.auth-token}")
    private String authToken;

    @Override
    public void registerNotification(RegisterNotificationRequest notificationRequest) throws JsonProcessingException {
        webClient.post()
                .uri("/notification/register")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
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
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(notificationRequest)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.err.println("Failed to send notification: " + e.getMessage()))
                .subscribe();
    }

    public void prepareSend(NotificationRequest event) {
        webClient.post()
                .uri("/notifications")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
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

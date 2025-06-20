package com.buddy.buddy.notification.Service;

import com.buddy.buddy.notification.DTO.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NotificationProducer {

    private final WebClient webClient;

    public NotificationProducer(WebClient webClient) {
        this.webClient = webClient;
    }

    public void sendNotification(NotificationRequest event) {
        webClient.post()
                .uri("/notifications")
                .bodyValue(event)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(e -> System.err.println("Failed to send notification: " + e.getMessage()))
                .subscribe();
    }
}

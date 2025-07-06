package com.buddy.buddy.notification.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogoutNotificationRequest {
    private String sub;
    private UUID userId;
    private UUID sessionId;
}

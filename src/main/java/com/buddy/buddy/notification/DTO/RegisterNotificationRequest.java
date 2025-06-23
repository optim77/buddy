package com.buddy.buddy.notification.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Data
@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
public class RegisterNotificationRequest {
    private UUID userId;
    private UUID sessionId;
    private String sub;
    private Long iat;
    private Long exp;
}

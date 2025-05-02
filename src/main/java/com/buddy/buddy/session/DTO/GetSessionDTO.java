package com.buddy.buddy.session.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSessionDTO {

    private UUID id;
    private String sessionId;
    private UUID userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String ip;
    private String agent;
    private String country;
}

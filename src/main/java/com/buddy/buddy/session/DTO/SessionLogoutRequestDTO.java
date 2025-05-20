package com.buddy.buddy.session.DTO;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionLogoutRequestDTO {

    private String sessionId;
}

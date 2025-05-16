package com.buddy.buddy.session.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionLogoutRequestDTO {

    private UUID sessionId;
}

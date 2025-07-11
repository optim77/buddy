package com.buddy.buddy.session.entity;

import com.buddy.buddy.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 400)
    private String session;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private String ip;

    @Column
    private String agent;

    @Column
    private String country;

    @Column
    private String countryCode;

    @Column
    private String city;

    @Column
    private String isp;

    @Column
    private String region;

    @Column
    private String timezone;

    @Column
    private String latitude;

    @Column
    private String longitude;
}

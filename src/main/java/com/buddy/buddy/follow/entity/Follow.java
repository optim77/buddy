package com.buddy.buddy.follow.entity;

import com.buddy.buddy.account.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private LocalDate created;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User followedTo;
}

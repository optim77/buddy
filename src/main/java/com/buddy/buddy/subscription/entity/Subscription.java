package com.buddy.buddy.subscription.entity;

import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.plan.entity.Plan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User subscriber;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User subscribedTo;

    @Column
    private boolean cancelled = false;

    @Column
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

}

package com.buddy.buddy.like.entity;


import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.image.entity.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "likes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "image_id", nullable = false)
    private Image image;

    @Column
    private LocalDate likedDate;

    @PrePersist
    private void createdAt(){
        this.likedDate = LocalDate.now();
    }


}

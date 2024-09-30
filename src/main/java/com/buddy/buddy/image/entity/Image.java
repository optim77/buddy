package com.buddy.buddy.image.entity;


import com.buddy.buddy.account.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Date uploadedDate;

    @Column(nullable = false, unique = true)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    User account;

    //likes
}

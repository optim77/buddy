package com.buddy.buddy.image.entity;


import com.buddy.buddy.account.entity.User;
import com.buddy.buddy.like.entity.Like;
import com.buddy.buddy.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
    private Date uploadedDate = new Date();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    User account;

    @Column(nullable = false, unique = true)
    private String description;

    @Column(nullable = true)
    private Date publishedDate;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column
    private boolean open;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "image", cascade = CascadeType.ALL)
    private Set<Like> likes;

    @ManyToMany
    @JoinTable(
            name = "image_tags",
            joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}

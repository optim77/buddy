package com.buddy.buddy.tag.entity;

import com.buddy.buddy.image.entity.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column
    private int count;

    @ManyToOne
    private Image image;

    @ManyToMany(mappedBy = "tags")
    private Set<Image> images = new HashSet<>();
}

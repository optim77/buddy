package com.buddy.buddy.tag.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @Column(unique = true, nullable = false)
    private String name;

    @Column
    private int count;

    @Column
    private String firstImage;

    @Column
    private String secondImage;

    @Column
    private String thirdImage;



}

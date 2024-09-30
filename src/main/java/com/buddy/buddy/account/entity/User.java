package com.buddy.buddy.account.entity;


import com.buddy.buddy.image.entity.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "buddy_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private short age;

    @Column
    private String avatar;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Image> photos = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private boolean deleted;

    @Column
    private boolean locked;

    @Column
    private boolean active;

    @Column(nullable = false, unique = false)
    private Date createdAt;

    @PrePersist
    private void createdAt(){
        this.createdAt = new Date();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    //origin
}

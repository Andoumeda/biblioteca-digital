package com.library.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "user_profiles")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserProfile extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 255)
    private String profilePicture;

    @OneToMany(mappedBy = "userProfile")
    private List<Publication> publications;

    @OneToMany(mappedBy = "userProfile")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "userProfile")
    private List<Favorite> favorites;

    @ManyToMany(mappedBy = "users")
    private List<Announcement> announcements;
}
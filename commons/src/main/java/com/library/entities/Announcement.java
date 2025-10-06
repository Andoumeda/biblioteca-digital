package com.library.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "announcements")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Announcement extends BaseEntity {
    public enum AnnouncementType {
        ALERT, INFO, WARNING, PROMO
    }

    public enum TargetAudience {
        ALL, NEW_USERS, ADMINS
    }

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnnouncementType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TargetAudience targetAudience;

    @ManyToMany
    @JoinTable(
            name = "announcement_user_profiles",
            joinColumns = @JoinColumn(name = "announcement_id"),
            inverseJoinColumns = @JoinColumn(name = "user_profile_id")
    )
    private List<UserProfile> recipients;
}
package com.library.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "profile_announcements")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProfileAnnouncement extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @Column(name = "programmed_date", nullable = false)
    private LocalDateTime programmedDate;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;
}

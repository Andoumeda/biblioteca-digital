package com.library.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "favorites")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Favorite extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;
}
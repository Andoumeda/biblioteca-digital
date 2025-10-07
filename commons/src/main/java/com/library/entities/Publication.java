package com.library.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "publications")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Publication extends BaseEntity {
    public enum PublicationState {
        APPROVED, PENDING, REJECTED
    }

    @ManyToOne
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(length = 255, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PublicationState state;

    @OneToMany(mappedBy = "publication")
    private List<Book> books;

    @OneToMany(mappedBy = "publication")
    private List<Favorite> favorites;

    @ManyToMany
    @JoinTable(
            name = "publication_categories",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}
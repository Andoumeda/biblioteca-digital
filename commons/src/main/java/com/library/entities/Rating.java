package com.library.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ratings")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Rating extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private Integer valoration;
}
package com.library.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "ratings")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Rating extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private Integer valoration;
}
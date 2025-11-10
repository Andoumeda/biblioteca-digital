package com.library.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "publication_categories")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PublicationCategory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "relevance_score", nullable = false)
    private Integer relevanceScore;
}

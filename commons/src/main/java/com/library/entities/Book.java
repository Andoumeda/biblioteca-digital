package com.library.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "books")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Book extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 255)
    private String bookUrl;

    @Column(length = 255)
    private String coverImg;

    @Column(nullable = false)
    @Min(1)
    @Max(2000)
    private Integer numPages;

    @Column(nullable = false)
    @Min(1)
    @Max(100000000) // Máximo 100 MB permitido, 100.000.000 bytes
    private Integer fileSize; // representado en bytes

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    @OneToMany(mappedBy = "book")
    private List<Rating> ratings;
}
package com.library.entities;

import jakarta.persistence.*;
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
    @ManyToOne(fetch = FetchType.LAZY)
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
    private Integer numPages;

    @Column(nullable = false)
    private Integer fileSize;

    @ManyToMany
    @JoinTable(
            name = "book_authors",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private List<Rating> ratings;
}
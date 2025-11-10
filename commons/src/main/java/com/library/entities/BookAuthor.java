package com.library.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "book_authors")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BookAuthor extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Column(name = "contribution_type", nullable = false, length = 30)
    private String contributionType;
}

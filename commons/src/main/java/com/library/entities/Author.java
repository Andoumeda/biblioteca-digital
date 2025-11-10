package com.library.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "authors")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Author extends BaseEntity {
    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String bio;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(length = 100)
    private String nationality;
}
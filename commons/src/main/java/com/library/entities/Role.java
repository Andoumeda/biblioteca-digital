package com.library.entities;

import jakarta.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name = "roles")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;
}
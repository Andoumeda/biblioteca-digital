package com.library.books.repositories;

import com.library.entities.BookAuthor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookAuthorRepository extends JpaRepository<BookAuthor, Integer> {

    /**
     * Buscar relación por bookId, authorId, contributionType y no eliminada (isDeleted = false)
     */
    java.util.Optional<BookAuthor> findByBookIdAndAuthorIdAndContributionTypeAndIsDeletedFalse(
        Integer bookId, Integer authorId, String contributionType
    );

    // Metodo custom para filtrar por bookId, authorId y contributionType (con paginación)
    @Query("SELECT ba FROM BookAuthor ba WHERE " +
       "(:bookId IS NULL OR ba.book.id = :bookId) AND " +
       "(:authorId IS NULL OR ba.author.id = :authorId) AND " +
       "(:contribution IS NULL OR ba.contributionType = :contribution) AND " +
       "ba.isDeleted = false")
    Page<BookAuthor> findByFilters(
        @Param("bookId") Integer bookId,
        @Param("authorId") Integer authorId,
        @Param("contribution") String contribution,
        Pageable pageable
    );

    /**
     * Buscar relación por ID y no eliminada (isDeleted = false)
     */
    Optional<BookAuthor> findByIdAndIsDeletedFalse(Integer id);

}

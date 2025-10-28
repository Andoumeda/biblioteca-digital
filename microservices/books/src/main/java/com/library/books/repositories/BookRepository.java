package com.library.books.repositories;

import com.library.entities.Book;
import com.library.entities.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    /**
     * Buscar publication por ID que no esté eliminado
     */
    @Query("SELECT p FROM Publication p WHERE p.id = :publicationId AND p.isDeleted = false")
    Optional<Publication> findPublicationByIdAndIsDeletedFalse(@Param("publicationId") Integer publicationId);

    /**
     * Verificar si existe un UserProfile por su ID y no está eliminado
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Publication p WHERE p.id = :publicationId AND p.isDeleted = false")
    boolean existsPublicationByIdAndIsDeletedFalse(@Param("publicationId") Integer publicationId);

    /**
     * Buscar libro por ID que no esté eliminado
     */
    Optional<Book> findByIdAndIsDeletedFalse(Integer id);

    /**
     * Buscar libros por múltiples filtros (título, publicación, autor)
     */
    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.authors a WHERE " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%'))) AND " +
            "(:publicationId IS NULL OR b.publication.id = :publicationId) AND " +
            "(:authorId IS NULL OR a.id = :authorId) AND " +
            "b.isDeleted = false")
    Page<Book> findByFilters(@Param("title") String title,
                              @Param("publicationId") Integer publicationId,
                              @Param("authorId") Integer authorId,
                              Pageable pageable);
}
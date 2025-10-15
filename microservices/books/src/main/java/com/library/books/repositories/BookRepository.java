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
     * Buscar todos los libros que no estén eliminados
     */
    Page<Book> findByIsDeletedFalse(Pageable pageable);

    /**
     * Buscar libros por título (búsqueda parcial, case insensitive)
     */
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')) AND b.isDeleted = false")
    Page<Book> findByTitleContainingIgnoreCaseAndIsDeletedFalse(@Param("title") String title, Pageable pageable);

    /**
     * Buscar libros por publicación
     */
    Page<Book> findByPublicationIdAndIsDeletedFalse(Integer publicationId, Pageable pageable);

    /**
     * Buscar libros por autor
     */
    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.id = :authorId AND b.isDeleted = false")
    Page<Book> findByAuthorIdAndIsDeletedFalse(@Param("authorId") Integer authorId, Pageable pageable);

    /**
     * Buscar libro por ID que no esté eliminado
     */
    Optional<Book> findByIdAndIsDeletedFalse(Integer id);
}

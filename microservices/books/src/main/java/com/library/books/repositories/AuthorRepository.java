package com.library.books.repositories;

import com.library.entities.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {

    /**
     * Buscar autor por ID que no esté eliminado
     */
    Optional<Author> findByIdAndIsDeletedFalse(Integer id);

    /**
     * Buscar autores por múltiples filtros (nombre, rango de fechas, nacionalidad, y opcionalmente libro)
     */
    @Query("SELECT DISTINCT a FROM Author a LEFT JOIN a.books b WHERE " +
            "(:fullName IS NULL OR LOWER(a.fullName) LIKE LOWER(CONCAT('%', CAST(:fullName AS string), '%'))) AND " +
            "(:minDate IS NULL OR a.birthDate >= :minDate) AND " +
            "(:maxDate IS NULL OR a.birthDate <= :maxDate) AND " +
            "(:nationality IS NULL OR LOWER(a.nationality) LIKE LOWER(CONCAT('%', CAST(:nationality AS string), '%'))) AND " +
            "(:bookId IS NULL OR b.id = :bookId) AND " +
            "a.isDeleted = false")
    Page<Author> findByFilters(
            @Param("bookId") Integer bookId,
            @Param("fullName") String fullName,
            @Param("minDate") LocalDate minDate,
            @Param("maxDate") LocalDate maxDate,
            @Param("nationality") String nationality,
            Pageable pageable);
}
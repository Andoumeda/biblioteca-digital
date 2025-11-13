package com.library.publications.repositories;

import com.library.entities.PublicationCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublicationCategoryRepository extends JpaRepository<PublicationCategory, Integer> {

    Optional<PublicationCategory> findByPublicationIdAndCategoryIdAndIsDeletedFalse(
        Integer publicationId, Integer categoryId
    );

    @Query("SELECT pc FROM PublicationCategory pc WHERE " +
           "(:publicationId IS NULL OR pc.publication.id = :publicationId) AND " +
           "(:categoryId IS NULL OR pc.category.id = :categoryId) AND " +
           "(:minRelevance IS NULL OR pc.relevanceScore >= :minRelevance) AND " +
           "(:maxRelevance IS NULL OR pc.relevanceScore <= :maxRelevance) AND " +
           "pc.isDeleted = false")
    Page<PublicationCategory> findByFilters(
        @Param("publicationId") Integer publicationId,
        @Param("categoryId") Integer categoryId,
        @Param("minRelevance") Integer minRelevance,
        @Param("maxRelevance") Integer maxRelevance,
        Pageable pageable
    );

    Optional<PublicationCategory> findByIdAndIsDeletedFalse(Integer id);
}

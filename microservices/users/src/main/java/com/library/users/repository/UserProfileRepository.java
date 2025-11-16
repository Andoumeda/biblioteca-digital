package com.library.users.repository;

import com.library.entities.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.library.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    boolean existsByUserId(Long userId);
    java.util.Optional<UserProfile> findByIdAndIsDeletedFalse(Integer id);

    @Query("SELECT u FROM UserProfile up JOIN up.user u WHERE up.id = :profileId AND u.isDeleted = false")
    User findActiveUserByProfileId(@Param("profileId") Integer profileId);

    boolean existsByIdAndIsDeletedFalse(Integer userId);

    /**
     * Filtro flexible por displayName (opcional)
     */
    @Query("""
        SELECT up FROM UserProfile up
        WHERE up.isDeleted = false
        AND up.displayName LIKE CONCAT('%', :displayName, '%')
    """)
    Page<UserProfile> findByDisplayNameLike(
        @Param("displayName") String displayName,
        Pageable pageable
    );

    Page<UserProfile> findByIsDeletedFalse(Pageable pageable);

}

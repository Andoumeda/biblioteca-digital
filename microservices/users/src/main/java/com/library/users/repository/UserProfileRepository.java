package com.library.users.repository;

import com.library.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.library.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    boolean existsByUserId(Long userId);
    java.util.Optional<UserProfile> findByIdAndIsDeletedFalse(Integer id);
    java.util.List<UserProfile> findAllByIsDeletedFalse();

    @Query("SELECT u FROM UserProfile up JOIN up.user u WHERE up.id = :profileId AND u.isDeleted = false")
    User findActiveUserByProfileId(@Param("profileId") Integer profileId);
}

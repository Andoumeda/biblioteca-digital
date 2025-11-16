package com.library.security.repository;

import com.library.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    Optional<UserProfile> findByUserIdAndIsDeletedFalse(Integer userId);
    Optional<UserProfile> findByIdAndIsDeletedFalse(Integer id);
}

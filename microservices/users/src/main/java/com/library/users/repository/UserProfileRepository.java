package com.library.users.repository;

import com.library.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {
    List<UserProfile> findByUserIdAndIsDeletedFalse(Long userId);
    boolean existsByUserId(Long userId);
}

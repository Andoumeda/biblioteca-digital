package com.library.users.services;

import com.library.dtos.UserProfileRequestDTO;
import com.library.dtos.UserProfileResponseDTO;
import java.util.List;

public interface UserProfileService {

    UserProfileResponseDTO createProfile(UserProfileRequestDTO dto);

    UserProfileResponseDTO getProfileById(Integer id);

    List<UserProfileResponseDTO> getAllProfiles();

    UserProfileResponseDTO updateProfile(Integer id, UserProfileRequestDTO dto);

    void deleteProfile(Integer id);
}

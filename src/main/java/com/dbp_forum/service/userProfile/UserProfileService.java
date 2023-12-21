package com.dbp_forum.service.userProfile;

import com.dbp_forum.dto.UserProfileDto;

public interface UserProfileService {
    UserProfileDto getUserProfile(Long userId);
    UserProfileDto getUserProfile(String username);
}

package com.hinduprayerlock.backend.mapper;

import com.hinduprayerlock.backend.model.UserEntity;
import com.hinduprayerlock.backend.model.dto.UserResponse;

public class UserMapper {

    public static UserResponse map(UserEntity user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
//                user.getPhoneNumber(),
                user.getIsSubscribed(),
                user.getCreatedAt()
        );
    }

}

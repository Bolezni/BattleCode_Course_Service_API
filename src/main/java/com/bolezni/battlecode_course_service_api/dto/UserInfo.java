package com.bolezni.battlecode_course_service_api.dto;

import java.util.List;

public record UserInfo(
        String userId,
        String username,
        String email,
        List<String> roles,
        String avatarUrl,
        String bio,
        boolean isVerified
) {
}

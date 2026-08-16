package org.example.User.DTO;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(max = 100) String fullName,
        String avatarUrl
) {}
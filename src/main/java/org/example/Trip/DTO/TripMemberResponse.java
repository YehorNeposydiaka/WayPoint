package org.example.Trip.DTO;

import org.example.Trip.Entity.MemberRole;

import java.time.Instant;
import java.util.UUID;

public record TripMemberResponse(
        UUID userId,
        String fullName,
        String email,
        MemberRole role,
        Instant joinedAt
) {}
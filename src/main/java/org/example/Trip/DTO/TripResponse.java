package org.example.Trip.DTO;

import org.example.Trip.Entity.TripStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TripResponse(
        UUID id,
        String title,
        String description,
        String coverUrl,
        TripStatus status,
        UUID inviteCode,
        LocalDate startDate,
        LocalDate endDate,
        UUID ownerId,
        String ownerName,
        Instant createdAt
) {}
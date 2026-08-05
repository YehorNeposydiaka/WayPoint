package org.example.Trip.DTO;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTripRequest(
        @Size(max = 150) String title,
        String description,
        String coverUrl,
        LocalDate startDate,
        LocalDate endDate
) {}
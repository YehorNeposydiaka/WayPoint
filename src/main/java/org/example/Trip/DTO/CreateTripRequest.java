package org.example.Trip.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTripRequest(
        @NotBlank @Size(max = 150) String title,
        String description,
        String coverUrl,
        LocalDate startDate,
        LocalDate endDate
) {}
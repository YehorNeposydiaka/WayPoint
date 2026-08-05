package org.example.Trip.DTO;

import jakarta.validation.constraints.NotNull;
import org.example.Trip.Entity.TripStatus;

public record UpdateTripStatusRequest (
        @NotNull TripStatus status
        ){}

package org.example.Transfer.DTO;

import org.example.Transfer.Entity.TransferType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse (
        UUID id,
        String title,
        String note,
        TransferType type,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String ticketUrl,
        BigDecimal cost,
        String departureLocation,
        String arrivalLocation,
        UUID tripId,
        Instant createdAt
){}

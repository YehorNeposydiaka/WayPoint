package org.example.Transfer.DTO;

import jakarta.validation.constraints.Size;
import org.example.Transfer.Entity.TransferType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateTransferRequest(
        @Size(max = 100) String title,
        String note,
        TransferType transferType,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        BigDecimal cost,
        String ticketUrl,
        String departureLocation,
        String arrivalLocation
) {}
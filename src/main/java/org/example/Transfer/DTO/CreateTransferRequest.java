package org.example.Transfer.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.Transfer.Entity.TransferType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateTransferRequest (
        @NotBlank @Size(max = 100) String title,
        String note,
        TransferType transferType,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal cost,
        String ticketUrl,
        String departureLocation,
        String arrivalLocation
){}

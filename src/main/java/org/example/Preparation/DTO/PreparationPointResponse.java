package org.example.Preparation.DTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record PreparationPointResponse (
        UUID id,
        String title,
        String note,
        LocalDateTime deadline,
        String attachmentLink,
        BigDecimal cost,
        UUID tripId,
        UUID assignedMemberId,
        boolean completed,
        Instant createdAt
){}

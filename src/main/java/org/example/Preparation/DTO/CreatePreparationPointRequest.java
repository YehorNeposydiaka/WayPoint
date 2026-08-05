package org.example.Preparation.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePreparationPointRequest (
        @NotBlank @Size(max = 100) String title,
        String note,
        LocalDateTime deadline,
        String attachmentLink,
        BigDecimal cost
){}

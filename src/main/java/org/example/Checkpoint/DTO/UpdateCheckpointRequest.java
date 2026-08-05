package org.example.Checkpoint.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.Checkpoint.Entity.CheckpointType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UpdateCheckpointRequest (
        @Size(max = 100) String title,
        String note,
        CheckpointType checkpointType,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal cost,
        String location
){}

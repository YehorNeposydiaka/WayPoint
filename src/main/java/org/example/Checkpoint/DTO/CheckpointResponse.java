package org.example.Checkpoint.DTO;

import org.example.Checkpoint.Entity.CheckpointType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CheckpointResponse (
        UUID id,
        String title,
        String note,
        CheckpointType checkpointType,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal cost,
        String location,
        UUID tripId,
        Instant createOn
){}

package org.example.Checkpoint.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.Checkpoint.Entity.CheckpointType;
import org.example.Transfer.Entity.TransferType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateCheckpointRequest (
        @NotBlank @Size(max = 100) String title,
        String note,
        CheckpointType checkpointType,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal cost,
        String location
){}

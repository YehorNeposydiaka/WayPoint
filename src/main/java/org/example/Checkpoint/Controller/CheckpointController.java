package org.example.Checkpoint.Controller;

import jakarta.validation.Valid;
import org.example.Checkpoint.DTO.CheckpointResponse;
import org.example.Checkpoint.DTO.CreateCheckpointRequest;
import org.example.Checkpoint.DTO.UpdateCheckpointRequest;
import org.example.Checkpoint.Service.CheckpointService;
import org.example.Security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips/{tripId}/checkpoints")
public class CheckpointController {
    private final CheckpointService checkpointService;

    public CheckpointController(CheckpointService checkpointService){
        this.checkpointService = checkpointService;
    }

    @PostMapping
    public ResponseEntity<CheckpointResponse> createCheckpoint(@Valid @RequestBody CreateCheckpointRequest request,
                                                               @AuthenticationPrincipal UserPrincipal principal,
                                                               @PathVariable UUID tripId){
        CheckpointResponse response = checkpointService.createCheckpoint(tripId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CheckpointResponse>> getCheckpoints(@AuthenticationPrincipal UserPrincipal principal,
                                                                   @PathVariable UUID tripId){
        List<CheckpointResponse> response = checkpointService.getCheckpoints(tripId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{checkpointId}")
    public ResponseEntity<CheckpointResponse> getCheckpointById(@AuthenticationPrincipal UserPrincipal principal,
                                                                @PathVariable UUID tripId,
                                                                @PathVariable UUID checkpointId){
        CheckpointResponse response = checkpointService.getCheckpointById(checkpointId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{checkpointId}")
    public ResponseEntity<CheckpointResponse> updateCheckpoint(@Valid @RequestBody UpdateCheckpointRequest request,
                                                               @AuthenticationPrincipal UserPrincipal principal,
                                                               @PathVariable UUID tripId,
                                                               @PathVariable UUID checkpointId){
        CheckpointResponse response = checkpointService.updateCheckpoint(checkpointId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{checkpointId}")
    public ResponseEntity<Void> deleteCheckpoint(@AuthenticationPrincipal UserPrincipal principal,
                                                 @PathVariable UUID checkpointId,
                                                 @PathVariable UUID tripId){
        checkpointService.deleteCheckpoint(checkpointId, principal.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package org.example.Transfer.Controller;

import jakarta.validation.Valid;
import org.example.Security.UserPrincipal;
import org.example.Transfer.DTO.CreateTransferRequest;
import org.example.Transfer.DTO.TransferResponse;
import org.example.Transfer.DTO.UpdateTransferRequest;
import org.example.Transfer.Service.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips/{tripId}/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer(
            @Valid @RequestBody CreateTransferRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID tripId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transferService.createTransfer(tripId, request, principal.getId()));
    }

    @GetMapping
    public ResponseEntity<List<TransferResponse>> getTransfers(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID tripId) {
        return ResponseEntity.ok(transferService.getTransfers(tripId, principal.getId()));
    }

    @GetMapping("/{transferId}")
    public ResponseEntity<TransferResponse> getTransferById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID tripId,
            @PathVariable UUID transferId) {
        return ResponseEntity.ok(transferService.getTransferById(transferId, principal.getId()));
    }

    @PatchMapping("/{transferId}")
    public ResponseEntity<TransferResponse> updateTransfer(
            @Valid @RequestBody UpdateTransferRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID tripId,
            @PathVariable UUID transferId) {
        return ResponseEntity.ok(transferService.updateTransfer(transferId, request, principal.getId()));
    }

    @DeleteMapping("/{transferId}")
    public ResponseEntity<Void> deleteTransfer(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID tripId,
            @PathVariable UUID transferId) {
        transferService.deleteTransfer(transferId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
package org.example.Preparation.Controller;

import jakarta.validation.Valid;
import org.example.Preparation.DTO.CreatePreparationPointRequest;
import org.example.Preparation.DTO.PreparationPointResponse;
import org.example.Preparation.DTO.UpdatePreparationPointRequest;
import org.example.Preparation.Service.PreparationPointService;
import org.example.Security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips/{tripId}/preparations")
public class PreparationPointController {
    private final PreparationPointService preparationPointService;

    public PreparationPointController(PreparationPointService preparationPointService){
        this.preparationPointService = preparationPointService;
    }

    @PostMapping
    public ResponseEntity<PreparationPointResponse> createPreparationPoint(@Valid @RequestBody CreatePreparationPointRequest request,
                                                               @AuthenticationPrincipal UserPrincipal principal,
                                                               @PathVariable UUID tripId){
        PreparationPointResponse response = preparationPointService.createPreparation(tripId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PreparationPointResponse>> getPreparationPoints(@AuthenticationPrincipal UserPrincipal principal,
                                                                   @PathVariable UUID tripId){
        List<PreparationPointResponse> response = preparationPointService.getPreparationPoints(tripId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{preparationPointId}")
    public ResponseEntity<PreparationPointResponse> getPreparationPointById(@AuthenticationPrincipal UserPrincipal principal,
                                                                @PathVariable UUID tripId,
                                                                @PathVariable UUID preparationPointId){
        PreparationPointResponse response = preparationPointService.getPreparationPointById(preparationPointId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{preparationPointId}")
    public ResponseEntity<PreparationPointResponse> updatePreparationPoint(@Valid @RequestBody UpdatePreparationPointRequest request,
                                                               @AuthenticationPrincipal UserPrincipal principal,
                                                               @PathVariable UUID tripId,
                                                               @PathVariable UUID preparationPointId){
        PreparationPointResponse response = preparationPointService.updatePreparationPoint(preparationPointId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{preparationPointId}/assign/{userId}")
    public ResponseEntity<PreparationPointResponse> assignToPreparationPoint(@AuthenticationPrincipal UserPrincipal principal,
                                                                           @PathVariable UUID tripId,
                                                                           @PathVariable UUID preparationPointId,
                                                                             @PathVariable UUID userId){
        PreparationPointResponse response = preparationPointService.assignMember(preparationPointId, principal.getId(), userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{preparationPointId}/complete")
    public ResponseEntity<PreparationPointResponse> completePreparationPoint(@AuthenticationPrincipal UserPrincipal principal,
                                                                             @PathVariable UUID tripId,
                                                                             @PathVariable UUID preparationPointId){
        PreparationPointResponse response = preparationPointService.toggleComplete(preparationPointId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PreparationPointResponse>> getUserPreparationPoints(@AuthenticationPrincipal UserPrincipal principal,
                                                                             @PathVariable UUID tripId,
                                                                             @PathVariable UUID userId){
        List<PreparationPointResponse> response = preparationPointService.getUserPreparationPoints(tripId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/{preparationPointId}")
    public ResponseEntity<Void> deletePreparationPoint(@AuthenticationPrincipal UserPrincipal principal,
                                                       @PathVariable UUID tripId,
                                                       @PathVariable UUID preparationPointId){
        preparationPointService.deletePreparationPoint(preparationPointId, principal.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package org.example.Trip.Controller;

import jakarta.validation.Valid;
import org.example.Security.UserPrincipal;
import org.example.Trip.DTO.*;
import org.example.Trip.Service.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "http://localhost:5173")
public class TripController {
    private final TripService tripService;
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody CreateTripRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        TripResponse response = tripService.createTrip(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TripResponse>> getUserTrips(@AuthenticationPrincipal UserPrincipal principal){
        List<TripResponse> responses = tripService.getUserTrips(principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTripById(@AuthenticationPrincipal UserPrincipal principal,
                                                    @PathVariable UUID tripId){
        TripResponse response = tripService.getTripById(tripId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{tripId}")
    public ResponseEntity<TripResponse> updateTrip(@Valid @RequestBody UpdateTripRequest request,
                                                   @AuthenticationPrincipal UserPrincipal principal,
                                                   @PathVariable UUID tripId){
        TripResponse response = tripService.updateTrip(tripId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/join/{inviteCode}")
    public ResponseEntity<TripResponse> addMemberToTrip(@AuthenticationPrincipal UserPrincipal principal,
                                                        @PathVariable UUID inviteCode){
        TripResponse response = tripService.addMemberToTrip(inviteCode, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{tripId}/members/{userId}/role")
    public ResponseEntity<TripMemberResponse> updateMemberRole(@Valid @RequestBody UpdateMemberRoleRequest requestRole,
                                                               @AuthenticationPrincipal UserPrincipal principal,
                                                               @PathVariable UUID tripId,
                                                               @PathVariable UUID userId){
        TripMemberResponse response = tripService.updateMemberRole(tripId, principal.getId(), requestRole, userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{tripId}/members")
    public ResponseEntity<List<TripMemberResponse>> getTripMembers(@AuthenticationPrincipal UserPrincipal principal,
                                                                   @PathVariable UUID tripId){
        List<TripMemberResponse> response = tripService.getTripMembers(tripId, principal.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{tripId}/status")
    public ResponseEntity<TripResponse> updateTripStatus(@Valid @RequestBody UpdateTripStatusRequest requestStatus,
                                                         @AuthenticationPrincipal UserPrincipal principal,
                                                         @PathVariable UUID tripId){
        TripResponse response = tripService.updateTripStatus(tripId, principal.getId(), requestStatus);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@AuthenticationPrincipal UserPrincipal principal,
                                           @PathVariable UUID tripId){
        tripService.deleteTrip(tripId, principal.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{tripId}/members/me")
    public ResponseEntity<Void> leaveTrip(@AuthenticationPrincipal UserPrincipal principal,
                                          @PathVariable UUID tripId){
        tripService.leaveTrip(tripId, principal.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{tripId}/members/{userId}")
    public ResponseEntity<Void> removeTripMember(@AuthenticationPrincipal UserPrincipal principal,
                                                 @PathVariable UUID tripId,
                                                 @PathVariable UUID userId){
        tripService.removeTripMember(tripId, principal.getId(), userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

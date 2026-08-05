package org.example.Trip.Service;


import org.example.Trip.DTO.*;
import org.example.Trip.Entity.MemberRole;
import org.example.Trip.Entity.Trip;
import org.example.Trip.Entity.TripMember;
import org.example.Trip.Entity.TripStatus;
import org.example.Trip.Repository.TripMemberRepository;
import org.example.Trip.Repository.TripRepository;
import org.example.User.Entity.User;
import org.example.User.Repository.UserRepository;
import org.example.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TripAccessService tripAccessService;

    public TripService(TripRepository tripRepository,
                       UserRepository userRepository,
                       TripMemberRepository tripMemberRepository,
                       TripAccessService tripAccessService){
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.tripMemberRepository = tripMemberRepository;
        this.tripAccessService = tripAccessService;
    }

@Transactional
    public TripResponse createTrip(CreateTripRequest request, UUID userId){
        User owner = userRepository.getReferenceById(userId);
        Trip trip = Trip.builder()
                 .title(request.title())
                 .description(request.description())
                 .coverUrl(request.coverUrl())
                 .startDate(request.startDate())
                 .endDate(request.endDate())
                 .owner(owner).build();
        TripMember creator = new TripMember(trip, owner, MemberRole.OWNER);

        tripRepository.save(trip);
        tripMemberRepository.save(creator);

        return toTripResponse(trip);
    }

    @Transactional
    public TripResponse addMemberToTrip(UUID inviteCode, UUID memberId){
        Trip trip = tripRepository.findByInviteCode(inviteCode)
        .orElseThrow(() -> new ApiException("Trip not found", HttpStatus.NOT_FOUND));

        if(tripMemberRepository.existsByTrip_IdAndUser_Id(trip.getId(), memberId)){
            throw new ApiException("User is already a member of trip", HttpStatus.CONFLICT);
        }

        User member = userRepository.getReferenceById(memberId);
        TripMember tripMember = new TripMember(trip, member, MemberRole.MEMBER);
        tripMemberRepository.save(tripMember);

        return toTripResponse(trip);
    }

    @Transactional
    public TripMemberResponse updateMemberRole(UUID tripId, UUID ownerId, UpdateMemberRoleRequest requestRole, UUID member){
        Trip trip = tripAccessCheck(tripId, ownerId);
        if(!tripAccessService.isOwner(tripId, ownerId)){
            throw new ApiException("Only owner can change member roles", HttpStatus.FORBIDDEN);
        }
        TripMember target = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, member)
                .orElseThrow(() -> new ApiException("User must be a member of trip to change role", HttpStatus.FORBIDDEN));

        if(member.equals(ownerId)){
            throw new ApiException("Owner cannot change their role", HttpStatus.FORBIDDEN);
        }
        if(requestRole.role() == MemberRole.OWNER){
            throw new ApiException("Not valid role", HttpStatus.FORBIDDEN);
        }
        target.setRole(requestRole.role());
        tripMemberRepository.save(target);
        return new TripMemberResponse(
                target.getUser().getId(),
                target.getUser().getFullName(),
                target.getUser().getEmail(),
                target.getRole(),
                target.getJoinedAt()
        );
    }

    public List<TripResponse> getUserTrips(UUID userId){
        List<Trip> trips = tripMemberRepository.findAllTripByUser_Id(userId);

        return trips.stream()
                .map(trip -> toTripResponse(trip))
                .toList();
    }

    public TripResponse getTripById(UUID tripId, UUID userId){
        Trip trip = tripAccessCheck(tripId, userId);

        return toTripResponse(trip);
    }

    public TripResponse updateTrip(UUID tripId, UpdateTripRequest request, UUID userId){
        Trip trip = tripAccessCheck(tripId, userId);
        if(!tripAccessService.isOwner(tripId, userId)){
            throw new ApiException("Only owner can edit trip", HttpStatus.FORBIDDEN);
        }

        if(request.title() != null)trip.setTitle(request.title());
        if(request.description() != null)trip.setDescription(request.description());
        if(request.coverUrl() != null)trip.setCoverUrl(request.coverUrl());
        if(request.startDate() != null)trip.setStartDate(request.startDate());
        if(request.endDate() != null)trip.setEndDate(request.endDate());

        tripRepository.save(trip);
        return toTripResponse(trip);
    }

    public List<TripMemberResponse> getTripMembers(UUID tripId, UUID userId){
        Trip trip = tripAccessCheck(tripId, userId);
        List<TripMember> members = tripMemberRepository.findAllUserByTrip_Id(tripId);
        return members.stream()
                .map(member -> toTripMemberResponse(member)).toList();
    }

    public TripResponse updateTripStatus(UUID tripId, UUID userId, UpdateTripStatusRequest status){
        Trip trip = tripAccessCheck(tripId, userId);
        if(!tripAccessService.isOwner(tripId, userId)){
            throw new ApiException("Only owner can edit trip", HttpStatus.FORBIDDEN);
        }
        if (status.status() == TripStatus.DELETED) {
            throw new ApiException("Use delete endpoint to delete a trip", HttpStatus.BAD_REQUEST);
        }
        if(trip.getStatus() == status.status()){
            throw new ApiException("This status is already set", HttpStatus.CONFLICT);
        }

        trip.setStatus(status.status());
        tripRepository.save(trip);

        return toTripResponse(trip);
    }

    public void deleteTrip(UUID tripId, UUID userId){
        Trip trip = tripAccessCheck(tripId, userId);
        if(!tripAccessService.isOwner(tripId, userId)){
            throw new ApiException("Only owner can delete trip", HttpStatus.FORBIDDEN);
        }
        tripMemberRepository.deleteAllByTrip_IdAndRoleNot(tripId, MemberRole.OWNER);
        trip.setStatus(TripStatus.DELETED);
        tripRepository.save(trip);
    }

    @Transactional
    public void leaveTrip(UUID tripId, UUID userId){
        Trip trip = tripAccessCheck(tripId, userId);
        if(tripAccessService.isOwner(tripId, userId)){
            throw new ApiException("Owner cannot leave trip", HttpStatus.FORBIDDEN);
        }
        TripMember member = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, userId)
                .orElseThrow(() -> new ApiException("Trip member not found", HttpStatus.NOT_FOUND));
        tripMemberRepository.delete(member);
    }

    @Transactional
    public List<TripMemberResponse> removeTripMember(UUID tripId, UUID ownerId, UUID userToRemoveId){
        Trip trip = tripAccessCheck(tripId, ownerId);
        if(!tripAccessService.isOwner(tripId, ownerId)){
            throw new ApiException("Only owner can remove trip member", HttpStatus.FORBIDDEN);
        }
        if(ownerId.equals(userToRemoveId)){
            throw new ApiException("Owner cannot be removed", HttpStatus.BAD_REQUEST);
        }
        TripMember target = tripMemberRepository.findByTrip_IdAndUser_Id(tripId, userToRemoveId)
                .orElseThrow(() -> new ApiException("Target user is not member of trip", HttpStatus.FORBIDDEN));

        tripMemberRepository.delete(target);
        List<TripMember> response= tripMemberRepository.findAllUserByTrip_Id(tripId);

        return response.stream()
                .map(member -> toTripMemberResponse(member)).toList();
    }

    private Trip tripAccessCheck(UUID tripId, UUID userId){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", HttpStatus.NOT_FOUND));
        if(!tripAccessService.isMember(tripId, userId)){
            throw new ApiException("User is not member of trip", HttpStatus.FORBIDDEN);
        }
        return trip;
    }

    private TripResponse toTripResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getTitle(),
                trip.getDescription(),
                trip.getCoverUrl(),
                trip.getStatus(),
                trip.getInviteCode(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getOwner().getId(),
                trip.getOwner().getFullName(),
                trip.getCreatedAt()
        );
    }
    private TripMemberResponse toTripMemberResponse(TripMember member){
        return new TripMemberResponse(
                member.getId(),
                member.getUser().getFullName(),
                member.getUser().getEmail(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}

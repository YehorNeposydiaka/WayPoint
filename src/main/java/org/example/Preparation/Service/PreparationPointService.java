package org.example.Preparation.Service;

import org.example.Preparation.DTO.CreatePreparationPointRequest;
import org.example.Preparation.DTO.PreparationPointResponse;
import org.example.Preparation.DTO.UpdatePreparationPointRequest;
import org.example.Preparation.Entity.PreparationPoint;
import org.example.Preparation.Repository.PreparationPointRepository;
import org.example.Trip.Entity.Trip;
import org.example.Trip.Entity.TripMember;
import org.example.Trip.Repository.TripMemberRepository;
import org.example.Trip.Repository.TripRepository;
import org.example.Trip.Service.TripAccessService;
import org.example.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PreparationPointService {
    private final PreparationPointRepository preparationPointRepository;
    private final TripAccessService tripAccessService;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;

    public PreparationPointService(PreparationPointRepository preparationPointRepository,
                                   TripAccessService tripAccessService,
                                   TripRepository tripRepository,
                                   TripMemberRepository tripMemberRepository){
        this.preparationPointRepository = preparationPointRepository;
        this.tripAccessService = tripAccessService;
        this.tripRepository = tripRepository;
        this.tripMemberRepository = tripMemberRepository;
    }

    @Transactional
    public PreparationPointResponse createPreparation(UUID tripId, CreatePreparationPointRequest request, UUID userId){
        tripAccessCheck(tripId, userId);
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", HttpStatus.NOT_FOUND));

        if(!tripAccessService.isEditor(tripId, userId)){
            throw new ApiException("User do not have access to create Preparation point", HttpStatus.FORBIDDEN);
        }
        PreparationPoint point = PreparationPoint.builder().title(request.title())
                .note(request.note())
                .deadline(request.deadline())
                .attachmentLink(request.attachmentLink())
                .cost(request.cost())
                .isCompleted(false).build();

        point.setTrip(trip);
        preparationPointRepository.save(point);

        return toPreparationPointResponse(point);
    }

    @Transactional(readOnly = true)
    public List<PreparationPointResponse> getPreparationPoints(UUID tripId, UUID userId){
        tripAccessCheck(tripId, userId);
        List<PreparationPoint> points = preparationPointRepository.findAllByTrip_Id(tripId);

        return points.stream()
                .map(point -> toPreparationPointResponse(point)).toList();
    }

    @Transactional(readOnly = true)
    public List<PreparationPointResponse> getUserPreparationPoints(UUID tripId, UUID userId){
        tripAccessCheck(tripId, userId);
        List<PreparationPoint> points = preparationPointRepository.findByTripIdForUser(tripId, userId);

        return points.stream()
                .map(point -> toPreparationPointResponse(point)).toList();
    }

    @Transactional(readOnly = true)
    public PreparationPointResponse getPreparationPointById(UUID preparationPointId, UUID userId){
        PreparationPoint point = preparationPointRepository.findById(preparationPointId)
                .orElseThrow(() -> new ApiException("Preparation point not found", HttpStatus.NOT_FOUND));
        tripAccessCheck(point.getTrip().getId(), userId);

        return toPreparationPointResponse(point);
    }

    @Transactional
    public PreparationPointResponse updatePreparationPoint(UUID pointId, UpdatePreparationPointRequest request, UUID userId){
        PreparationPoint point = preparationPointRepository.findById(pointId)
                .orElseThrow(() -> new ApiException("Checkpoint not found", HttpStatus.NOT_FOUND));
        tripAccessCheck(point.getTrip().getId(), userId);

        if(!tripAccessService.isEditor(point.getTrip().getId(), userId)){
            throw new ApiException("User do not have access to edit point", HttpStatus.FORBIDDEN);
        }
        if(request.title() != null) point.setTitle(request.title());
        if(request.note() != null) point.setNote(request.note());
        if(request.deadline() != null) point.setDeadline(request.deadline());
        if(request.attachmentLink() != null) point.setAttachmentLink(request.attachmentLink());
        if(request.cost() != null) point.setCost(request.cost());

        preparationPointRepository.save(point);
        return toPreparationPointResponse(point);
    }

    @Transactional
    public PreparationPointResponse assignMember(UUID pointId, UUID userId, UUID targetId){
        PreparationPoint point = preparationPointRepository.findById(pointId)
                .orElseThrow(() -> new ApiException("Preparation point not found", HttpStatus.NOT_FOUND));

        UUID tripId = point.getTrip().getId();

        if (!tripAccessService.isMember(tripId, userId)) {
            throw new ApiException("User is not member of trip", HttpStatus.FORBIDDEN);
        }

        TripMember targetMember = tripMemberRepository
                .findByTrip_IdAndUser_Id(tripId, targetId)
                .orElseThrow(() -> new ApiException("Target user is not member of trip", HttpStatus.BAD_REQUEST));

        if (point.getAssignedMember() != null &&
                point.getAssignedMember().getUser() != null &&
                targetId.equals(point.getAssignedMember().getUser().getId())) {
            throw new ApiException("User is already assigned to this point", HttpStatus.CONFLICT);
        }

        point.setAssignedMember(targetMember);
        preparationPointRepository.save(point);

        return toPreparationPointResponse(point);
    }

    @Transactional
    public PreparationPointResponse toggleComplete(UUID pointId, UUID userId){
        PreparationPoint point = preparationPointRepository.findById(pointId)
                .orElseThrow(() -> new ApiException("Preparation point not found", HttpStatus.NOT_FOUND));

        UUID tripId = point.getTrip().getId();

        if (!tripAccessService.isMember(tripId, userId)) {
            throw new ApiException("User is not member of trip", HttpStatus.FORBIDDEN);
        }

        boolean isAssignedUser = Optional.ofNullable(point.getAssignedMember())
                .map(TripMember::getUser)
                .map(user -> userId.equals(user.getId()))
                .orElse(false);

        boolean isTripOwner = Optional.ofNullable(point.getTrip())
                .map(Trip::getOwner)
                .map(owner -> userId.equals(owner.getId()))
                .orElse(false);

        if (isAssignedUser || isTripOwner) {
            point.setCompleted(true);
            preparationPointRepository.save(point);
        } else {
            throw new ApiException("User have not access to complete this task", HttpStatus.FORBIDDEN);
        }
        return toPreparationPointResponse(point);
    }

    @Transactional
    public void deletePreparationPoint(UUID pointId, UUID userId){
        PreparationPoint point = preparationPointRepository.findById(pointId)
                .orElseThrow(() -> new ApiException("Preparation point not found", HttpStatus.NOT_FOUND));
        tripAccessCheck(point.getTrip().getId(), userId);

        if(!tripAccessService.isEditor(point.getTrip().getId(), userId)){
            throw new ApiException("User do not have access to delete preparation point", HttpStatus.FORBIDDEN);
        }
        preparationPointRepository.delete(point);
    }

    private void tripAccessCheck(UUID tripId, UUID userId){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", HttpStatus.NOT_FOUND));
        if(!tripAccessService.isMember(tripId, userId)){
            throw new ApiException("User is not member of trip", HttpStatus.FORBIDDEN);
        }
    }

    private PreparationPointResponse toPreparationPointResponse(PreparationPoint point){
        UUID assignedUserId = Optional.ofNullable(point.getAssignedMember())
                .map(TripMember::getUser)
                .map(user -> user.getId())
                .orElse(null);

        return new PreparationPointResponse(point.getId(),
                point.getTitle(),
                point.getNote(),
                point.getDeadline(),
                point.getAttachmentLink(),
                point.getCost(),
                point.getTrip().getId(),
                point.getAssignedMember() != null ? point.getAssignedMember().getUser().getId() : null,
                point.isCompleted(),
                point.getCreatedAt());
    }
}
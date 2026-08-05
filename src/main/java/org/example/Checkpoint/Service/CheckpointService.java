package org.example.Checkpoint.Service;

import org.example.Checkpoint.DTO.CheckpointResponse;
import org.example.Checkpoint.DTO.CreateCheckpointRequest;
import org.example.Checkpoint.DTO.UpdateCheckpointRequest;
import org.example.Checkpoint.Entity.Checkpoint;
import org.example.Checkpoint.Repository.CheckpointRepository;
import org.example.Trip.Entity.Trip;
import org.example.Trip.Repository.TripRepository;
import org.example.Trip.Service.TripAccessService;
import org.example.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CheckpointService {
    private final CheckpointRepository checkpointRepository;
    private final TripAccessService tripAccessService;
    private final TripRepository tripRepository;

    public CheckpointService(CheckpointRepository checkpointRepository,
                             TripAccessService tripAccessService,
                             TripRepository tripRepository){
        this.checkpointRepository = checkpointRepository;
        this.tripAccessService = tripAccessService;
        this.tripRepository = tripRepository;
    }

    @Transactional
    public CheckpointResponse createCheckpoint(UUID tripId, CreateCheckpointRequest request, UUID userId){
        tripAccessCheck(tripId, userId);
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", HttpStatus.NOT_FOUND));

        if(!tripAccessService.isEditor(tripId, userId)){
            throw new ApiException("User do not have access to create checkpoint", HttpStatus.FORBIDDEN);
        }
        Checkpoint checkpoint = Checkpoint.builder().title(request.title())
                .note(request.note())
                .checkpointType(request.checkpointType())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .cost(request.cost())
                .location(request.location()).build();

        checkpoint.setTrip(trip);
        checkpointRepository.save(checkpoint);

        return toCheckpointResponse(checkpoint);
    }

    public List<CheckpointResponse> getCheckpoints(UUID tripId, UUID userId){
        tripAccessCheck(tripId, userId);
        List<Checkpoint> checkpoints = checkpointRepository.findAllCheckpointByTrip_Id(tripId);

        return checkpoints.stream()
                .map(checkpoint -> toCheckpointResponse(checkpoint)).toList();
    }

    public CheckpointResponse getCheckpointById(UUID checkpointId, UUID userId){
        Checkpoint checkpoint = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new ApiException("Checkpoint not found", HttpStatus.NOT_FOUND));
        tripAccessCheck(checkpoint.getTrip().getId(), userId);

        return toCheckpointResponse(checkpoint);
    }

    @Transactional
    public CheckpointResponse updateCheckpoint(UUID checkpointId, UpdateCheckpointRequest request, UUID userId){
        Checkpoint checkpoint = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new ApiException("Checkpoint not found", HttpStatus.NOT_FOUND));
        tripAccessCheck(checkpoint.getTrip().getId(), userId);

        if(!tripAccessService.isEditor(checkpoint.getTrip().getId(), userId)){
            throw new ApiException("User do not have access to edit checkpoint", HttpStatus.FORBIDDEN);
        }
        if(request.title() != null) checkpoint.setTitle(request.title());
        if(request.note() != null) checkpoint.setNote(request.note());
        if(request.checkpointType() != null) checkpoint.setCheckpointType(request.checkpointType());
        if(request.startTime() != null) checkpoint.setStartTime(request.startTime());
        if(request.endTime() != null) checkpoint.setEndTime(request.endTime());
        if(request.cost() != null) checkpoint.setCost(request.cost());
        if(request.location() != null) checkpoint.setLocation(request.location());

        checkpointRepository.save(checkpoint);
        return toCheckpointResponse(checkpoint);
    }

    @Transactional
    public void deleteCheckpoint(UUID checkpointId, UUID userId){
        Checkpoint checkpoint = checkpointRepository.findById(checkpointId)
                .orElseThrow(() -> new ApiException("Checkpoint not found", HttpStatus.NOT_FOUND));
        tripAccessCheck(checkpoint.getTrip().getId(), userId);

        if(!tripAccessService.isEditor(checkpoint.getTrip().getId(), userId)){
            throw new ApiException("User do not have access to create checkpoint", HttpStatus.FORBIDDEN);
        }
        checkpointRepository.delete(checkpoint);
    }
    private void tripAccessCheck(UUID tripId, UUID userId){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", HttpStatus.NOT_FOUND));
        if(!tripAccessService.isMember(tripId, userId)){
            throw new ApiException("User is not member of trip", HttpStatus.FORBIDDEN);
        }
    }

    private CheckpointResponse toCheckpointResponse(Checkpoint checkpoint){
        return new CheckpointResponse(checkpoint.getId(),
                checkpoint.getTitle(),
                checkpoint.getNote(),
                checkpoint.getCheckpointType(),
                checkpoint.getStartTime(),
                checkpoint.getEndTime(),
                checkpoint.getCost(),
                checkpoint.getLocation(),
                checkpoint.getTrip().getId(),
                checkpoint.getCreatedAt());
    }
}

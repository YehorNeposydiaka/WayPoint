package org.example.Trip.Service;


import org.example.Checkpoint.Entity.Checkpoint;
import org.example.Checkpoint.Entity.CheckpointType;
import org.example.Checkpoint.Repository.CheckpointRepository;
import org.example.Preparation.Entity.PreparationPoint;
import org.example.Preparation.Repository.PreparationPointRepository;
import org.example.Transfer.Entity.Transfer;
import org.example.Transfer.Entity.TransferType;
import org.example.Transfer.Repository.TransferRepository;
import org.example.Trip.DTO.TripStatResponse;
import org.example.Trip.Entity.Trip;
import org.example.Trip.Repository.TripRepository;
import org.example.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
public class TripStatService {
    private final TripRepository tripRepository;
    private final TransferRepository transferRepository;
    private final CheckpointRepository checkpointRepository;
    private final PreparationPointRepository preparationPointRepository;
    private final TripAccessService tripAccessService;

    public TripStatService(TripRepository tripRepository,
                       TransferRepository transferRepository,
                       CheckpointRepository checkpointRepository,
                       PreparationPointRepository preparationPointRepository,
                       TripAccessService tripAccessService){
        this.tripRepository = tripRepository;
        this.transferRepository = transferRepository;
        this.checkpointRepository = checkpointRepository;
        this.preparationPointRepository = preparationPointRepository;
        this.tripAccessService = tripAccessService;
    }

    public TripStatResponse getTripStat(UUID userId, UUID tripId){
        tripAccessCheck(tripId, userId);

        BigDecimal totalCost;

        BigDecimal transferTotalCost = BigDecimal.ZERO;
        BigDecimal planeTransferCost = BigDecimal.ZERO;
        BigDecimal carTransferCost = BigDecimal.ZERO;
        BigDecimal shipTransferCost = BigDecimal.ZERO;
        BigDecimal trainTransferCost = BigDecimal.ZERO;
        BigDecimal busTransferCost = BigDecimal.ZERO;
        BigDecimal otherTransferCost = BigDecimal.ZERO;
        int transfersAmount;

        BigDecimal foodCost = BigDecimal.ZERO;
        BigDecimal entertainmentCost = BigDecimal.ZERO;
        BigDecimal landmarkCost = BigDecimal.ZERO;
        BigDecimal accommodationCost = BigDecimal.ZERO;
        BigDecimal shoppingCost = BigDecimal.ZERO;
        BigDecimal otherCost = BigDecimal.ZERO;
        BigDecimal checkpointsCost = BigDecimal.ZERO;
        int checkpointsAmount;

        BigDecimal preparationCost = BigDecimal.ZERO;
        int preparationAmount;

        Long transferTime = 0L;

        List<Transfer> transfers = transferRepository.findAllByTrip_IdOrderByDepartureTimeAsc(tripId);
        for (Transfer transfer : transfers) {
            BigDecimal cost = transfer.getCost();

            if (transfer.getDepartureTime() != null && transfer.getArrivalTime() != null)
                transferTime += Duration.between(transfer.getDepartureTime(), transfer.getArrivalTime()).toMinutes();

            if (cost == null) continue;

            transferTotalCost = transferTotalCost.add(cost);

            if (transfer.getTransferType() != null) {
                switch (transfer.getTransferType()) {
                    case PLANE -> planeTransferCost = planeTransferCost.add(cost);
                    case CAR -> carTransferCost = carTransferCost.add(cost);
                    case SHIP -> shipTransferCost = shipTransferCost.add(cost);
                    case TRAIN -> trainTransferCost = trainTransferCost.add(cost);
                    case BUS -> busTransferCost = busTransferCost.add(cost);
                    case OTHER -> otherTransferCost = otherTransferCost.add(cost);
                }
            }
        }
        transfersAmount = transfers.size();

        List<Checkpoint> checkpoints = checkpointRepository.findAllCheckpointByTrip_Id(tripId);
        for (Checkpoint checkpoint : checkpoints) {
            BigDecimal cost = checkpoint.getCost();
            if (cost == null) continue;

            checkpointsCost = checkpointsCost.add(cost);

            if (checkpoint.getCheckpointType() != null) {
                switch (checkpoint.getCheckpointType()) {
                    case FOOD -> foodCost = foodCost.add(cost);
                    case ENTERTAINMENT -> entertainmentCost = entertainmentCost.add(cost);
                    case LANDMARK -> landmarkCost = landmarkCost.add(cost);
                    case ACCOMMODATION -> accommodationCost = accommodationCost.add(cost);
                    case SHOPPING -> shoppingCost = shoppingCost.add(cost);
                    case OTHER -> otherCost = otherCost.add(cost);
                }
            }
        }
        checkpointsAmount = checkpoints.size();

        List<PreparationPoint> preparationPoints = preparationPointRepository.findAllByTrip_Id(tripId);
        for(PreparationPoint point : preparationPoints){
            if(point.getCost() !=null)
                preparationCost = preparationCost.add(point.getCost());
        }
        preparationAmount = preparationPoints.size();

        totalCost = transferTotalCost.add(checkpointsCost).add(preparationCost);

        return new TripStatResponse(
                totalCost,
                transferTotalCost,
                planeTransferCost,
                carTransferCost,
                shipTransferCost,
                trainTransferCost,
                busTransferCost,
                otherTransferCost,
                transferTime,
                foodCost,
                entertainmentCost,
                landmarkCost,
                accommodationCost,
                shoppingCost,
                otherCost,
                checkpointsCost,
                transfersAmount,
                checkpointsAmount,
                preparationAmount,
                preparationCost
        );
    }
    private Trip tripAccessCheck(UUID tripId, UUID userId){
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", HttpStatus.NOT_FOUND));
        if(!tripAccessService.isMember(tripId, userId)){
            throw new ApiException("User is not member of trip", HttpStatus.FORBIDDEN);
        }
        return trip;
    }
}

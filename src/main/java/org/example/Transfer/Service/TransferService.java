package org.example.Transfer.Service;

import org.example.Transfer.DTO.CreateTransferRequest;
import org.example.Transfer.DTO.TransferResponse;
import org.example.Transfer.DTO.UpdateTransferRequest;
import org.example.Transfer.Entity.Transfer;
import org.example.Transfer.Repository.TransferRepository;
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
public class TransferService {

    private final TripRepository tripRepository;
    private final TransferRepository transferRepository;
    private final TripAccessService tripAccessService;

    public TransferService(TripRepository tripRepository,
                           TransferRepository transferRepository,
                           TripAccessService tripAccessService) {
        this.tripRepository = tripRepository;
        this.transferRepository = transferRepository;
        this.tripAccessService = tripAccessService;
    }

    @Transactional
    public TransferResponse createTransfer(UUID tripId, CreateTransferRequest request, UUID userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ApiException("Trip not found", HttpStatus.NOT_FOUND));

        if (!tripAccessService.isEditor(tripId, userId)) {
            throw new ApiException("Only editors and owners can create transfers", HttpStatus.FORBIDDEN);
        }

        Transfer transfer = Transfer.builder()
                .title(request.title())
                .note(request.note())
                .transferType(request.transferType())
                .departureTime(request.departureTime())
                .arrivalTime(request.arrivalTime())
                .cost(request.cost())
                .ticketUrl(request.ticketUrl())
                .departureLocation(request.departureLocation())
                .arrivalLocation(request.arrivalLocation())
                .trip(trip)
                .build();

        transferRepository.save(transfer);
        return toTransferResponse(transfer);
    }

    public List<TransferResponse> getTransfers(UUID tripId, UUID userId) {
        if (!tripAccessService.isMember(tripId, userId)) {
            throw new ApiException("User is not member of trip", HttpStatus.FORBIDDEN);
        }

        return transferRepository.findAllByTrip_IdOrderByDepartureTimeAsc(tripId)
                .stream()
                .map(this::toTransferResponse)
                .toList();
    }

    public TransferResponse getTransferById(UUID transferId, UUID userId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ApiException("Transfer not found", HttpStatus.NOT_FOUND));

        if (!tripAccessService.isMember(transfer.getTrip().getId(), userId)) {
            throw new ApiException("User is not member of trip", HttpStatus.FORBIDDEN);
        }

        return toTransferResponse(transfer);
    }

    @Transactional
    public TransferResponse updateTransfer(UUID transferId, UpdateTransferRequest request, UUID userId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ApiException("Transfer not found", HttpStatus.NOT_FOUND));

        if (!tripAccessService.isEditor(transfer.getTrip().getId(), userId)) {
            throw new ApiException("Only editors and owners can edit transfers", HttpStatus.FORBIDDEN);
        }

        if (request.title() != null) transfer.setTitle(request.title());
        if (request.note() != null) transfer.setNote(request.note());
        if (request.transferType() != null) transfer.setTransferType(request.transferType());
        if (request.departureTime() != null) transfer.setDepartureTime(request.departureTime());
        if (request.arrivalTime() != null) transfer.setArrivalTime(request.arrivalTime());
        if (request.cost() != null) transfer.setCost(request.cost());
        if (request.ticketUrl() != null) transfer.setTicketUrl(request.ticketUrl());
        if (request.departureLocation() != null) transfer.setDepartureLocation(request.departureLocation());
        if (request.arrivalLocation() != null) transfer.setArrivalLocation(request.arrivalLocation());

        transferRepository.save(transfer);
        return toTransferResponse(transfer);
    }

    @Transactional
    public void deleteTransfer(UUID transferId, UUID userId) {
        Transfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ApiException("Transfer not found", HttpStatus.NOT_FOUND));

        if (!tripAccessService.isEditor(transfer.getTrip().getId(), userId)) {
            throw new ApiException("Only editors and owners can delete transfers", HttpStatus.FORBIDDEN);
        }

        transferRepository.delete(transfer);
    }

    private TransferResponse toTransferResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getTitle(),
                transfer.getNote(),
                transfer.getTransferType(),
                transfer.getDepartureTime(),
                transfer.getArrivalTime(),
                transfer.getTicketUrl(),
                transfer.getCost(),
                transfer.getDepartureLocation(),
                transfer.getArrivalLocation(),
                transfer.getTrip().getId(),
                transfer.getCreatedAt()
        );
    }
}
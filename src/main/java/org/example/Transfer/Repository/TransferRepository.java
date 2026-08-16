package org.example.Transfer.Repository;

import org.example.Transfer.Entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
    List<Transfer> findAllByTrip_IdOrderByDepartureTimeAsc(UUID tripId);
}

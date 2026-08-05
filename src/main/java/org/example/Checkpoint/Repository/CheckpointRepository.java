package org.example.Checkpoint.Repository;

import org.example.Checkpoint.Entity.Checkpoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CheckpointRepository extends JpaRepository<Checkpoint, UUID> {
    List<Checkpoint> findAllCheckpointByTrip_Id(UUID tripId);
}

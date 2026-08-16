package org.example.Preparation.Repository;

import org.example.Preparation.Entity.PreparationPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PreparationPointRepository extends JpaRepository<PreparationPoint, UUID> {
    List<PreparationPoint> findAllByTrip_Id(UUID tripId);

    @Query("""
    SELECT p FROM PreparationPoint p
    WHERE p.trip.id = :tripId
    AND (p.assignedMember.user.id = :userId OR p.assignedMember IS NULL)
    """)
    List<PreparationPoint> findByTripIdForUser(
            @Param("tripId") UUID tripId,
            @Param("userId") UUID userId);
}

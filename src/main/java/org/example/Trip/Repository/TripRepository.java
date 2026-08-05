package org.example.Trip.Repository;

import org.example.Trip.Entity.Trip;
import org.example.User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    Optional<Trip> findByInviteCode(UUID inviteCode);
    List<Trip> findAllByOwner_Id(UUID userId);
}

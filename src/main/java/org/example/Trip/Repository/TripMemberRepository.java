package org.example.Trip.Repository;

import org.example.Trip.Entity.MemberRole;
import org.example.Trip.Entity.Trip;
import org.example.Trip.Entity.TripMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripMemberRepository extends JpaRepository<TripMember, UUID> {
    boolean existsByTrip_IdAndUser_Id(UUID tripId, UUID userId);
    Optional<TripMember> findByTrip_IdAndUser_Id(UUID tripId, UUID userId);
    @Query("SELECT tm.trip FROM TripMember tm WHERE tm.user.id = :userId")
    List<Trip> findAllTripsByUserId(@Param("userId") UUID userId);
    List<TripMember> findAllUserByTrip_Id(UUID tripId);
    void deleteAllByTrip_IdAndRoleNot(UUID tripId, MemberRole role);
}
package org.example.Trip.Service;

import org.example.Trip.Entity.MemberRole;
import org.example.Trip.Repository.TripMemberRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TripAccessService {

    private final TripMemberRepository tripMemberRepository;

    public TripAccessService(TripMemberRepository tripMemberRepository) {
        this.tripMemberRepository = tripMemberRepository;
    }

    public boolean isMember(UUID tripId, UUID userId) {
        return tripMemberRepository.existsByTrip_IdAndUser_Id(tripId, userId);
    }

    public boolean isOwner(UUID tripId, UUID userId) {
        return tripMemberRepository
                .findByTrip_IdAndUser_Id(tripId, userId)
                .map(member -> member.getRole() == MemberRole.OWNER)
                .orElse(false);
    }
    public boolean isEditor(UUID tripId, UUID userId) {
        return tripMemberRepository
                .findByTrip_IdAndUser_Id(tripId, userId)
                .map(member -> member.getRole() == MemberRole.EDITOR || member.getRole() == MemberRole.OWNER)
                .orElse(false);
    }
}
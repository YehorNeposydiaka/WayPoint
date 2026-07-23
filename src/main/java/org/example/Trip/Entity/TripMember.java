package org.example.Trip.Entity;

import org.example.User.Entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "trip_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trip_id", "user_id"})
        // один юзер не може бути доданий в ту саму кімнату двічі
)
@Getter
@Setter
@NoArgsConstructor
public class TripMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role = MemberRole.MEMBER;

    @Column(nullable = false, updatable = false)
    private Instant joinedAt;

    @PrePersist
    protected void onJoin() {
        this.joinedAt = Instant.now();
    }

    public TripMember(Trip trip, User user, MemberRole role) {
        this.trip = trip;
        this.user = user;
        this.role = role;
    }
}
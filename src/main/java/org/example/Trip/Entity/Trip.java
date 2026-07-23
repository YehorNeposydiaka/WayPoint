package org.example.Trip.Entity;

import org.example.User.Entity.User;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String coverUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TripStatus status = TripStatus.PLANNING;

    @Column(name = "invite_code", nullable = false, unique = true, updatable = false)
    @Builder.Default
    private UUID inviteCode = UUID.randomUUID();

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Власник кімнати — той, хто її створив. Дублює інформацію з RoomMember(role=OWNER),
    // але зручно мати пряме посилання для швидких перевірок без запиту в RoomMember.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private User owner;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TripMember> members = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.inviteCode == null) {
            this.inviteCode = UUID.randomUUID();
        }
    }

    // Зручний метод, щоб не лізти в members напряму з сервісу
    public void addMember(TripMember member) {
        members.add(member);
        member.setTrip(this);
    }
}

package org.example.Preparation.Entity;

import org.example.Trip.Entity.*;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "preparations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Preparation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String note;

    private LocalDateTime deadline;
    private String link;
    private BigDecimal cost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_member_id")
    private TripMember assignedMember;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private boolean isCompleted;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}

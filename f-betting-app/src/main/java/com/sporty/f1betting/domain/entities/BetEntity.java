package com.sporty.f1betting.domain.entities;

import com.sporty.f1betting.domain.model.Bet.BetStatus;
import com.sporty.f1betting.domain.enums.Odds;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class BetEntity {
    @Id
    private UUID id;
    private UUID userId;
    private UUID eventId;
    private UUID driverId;
    private double amount;

    @Enumerated(EnumType.STRING)
    private Odds odds;

    @Enumerated(EnumType.STRING)
    private BetStatus status;

    public BetEntity() {}

    public BetEntity(UUID id, UUID userId, UUID eventId, UUID driverId, double amount, Odds odds, BetStatus status) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.driverId = driverId;
        this.amount = amount;
        this.odds = odds;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getEventId() { return eventId; }
    public UUID getDriverId() { return driverId; }
    public double getAmount() { return amount; }
    public Odds getOdds() { return odds; }
    public BetStatus getStatus() { return status; }
}

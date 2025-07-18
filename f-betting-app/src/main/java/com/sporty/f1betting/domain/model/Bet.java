package com.sporty.f1betting.domain.model;

import com.sporty.f1betting.domain.enums.Odds;
import java.util.UUID;

public record Bet(
        UUID id,
        UUID userId,
        UUID eventId,
        UUID driverId,
        double amount,
        Odds odds,
        BetStatus status
) {
    public enum BetStatus {
        PLACED, WON, LOST
    }

    public Bet markWon() {
        return new Bet(id, userId, eventId, driverId, amount, odds, BetStatus.WON);
    }

    public Bet markLost() {
        return new Bet(id, userId, eventId, driverId, amount, odds, BetStatus.LOST);
    }

    public double winnings() {
        return amount * odds.getValue();
    }
}

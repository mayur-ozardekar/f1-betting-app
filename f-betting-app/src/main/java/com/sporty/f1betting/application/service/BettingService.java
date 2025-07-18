package com.sporty.f1betting.application.service;

import com.sporty.f1betting.api.exceptions.BettingServiceException;
import com.sporty.f1betting.application.port.BetRepositoryPort;
import com.sporty.f1betting.application.port.EventFetcherPort;
import com.sporty.f1betting.application.port.UserRepositoryPort;
import com.sporty.f1betting.domain.enums.Odds;
import com.sporty.f1betting.domain.model.*;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BettingService {

    private final UserRepositoryPort userRepo;
    private final BetRepositoryPort betRepo;
    private final EventFetcherPort eventFetcher;

    public BettingService(
            UserRepositoryPort userRepo,
            BetRepositoryPort betRepo,
            EventFetcherPort eventFetcher
    ) {
        this.userRepo = userRepo;
        this.betRepo = betRepo;
        this.eventFetcher = eventFetcher;
    }

    public Bet placeBet(UUID userId, UUID eventId, UUID driverId, double amount) {
        User user = userRepo.findById(userId).orElseThrow(() -> new BettingServiceException("User not found"));
        User updatedUser = user.debit(amount);
        userRepo.save(updatedUser);

        Odds odds = Odds.random();
        Bet bet = new Bet(UUID.randomUUID(), userId, eventId, driverId, amount, odds, Bet.BetStatus.PLACED);
        return betRepo.save(bet);
    }

    public List<Map<String, Object>> listEvents(String sessionType, Integer year, String country) {
        List<F1Event> events = eventFetcher.fetchEvents(sessionType, year, country);

        return events.stream().map(event -> Map.of(
                "event", event,
                "drivers", eventFetcher.fetchDriversForEvent(event.sessionKey()).stream().map(driver -> Map.of(
                        "driverId", driver.id(),
                        "fullName", driver.fullName(),
                        "odds", Odds.random().getValue()
                )).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

    public void simulateOutcome(UUID eventId, UUID winningDriverId) {
        List<Bet> bets = betRepo.findByEventId(eventId);
        for (Bet bet : bets) {
            if (bet.driverId().equals(winningDriverId)) {
                User user = userRepo.findById(bet.userId()).orElseThrow(() -> new BettingServiceException("User not found"));
                userRepo.save(user.credit(bet.winnings()));
                betRepo.save(bet.markWon());
            } else {
                betRepo.save(bet.markLost());
            }
        }
    }
}

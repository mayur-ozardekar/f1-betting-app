package com.sporty.f1betting.application.service;

import com.sporty.f1betting.api.exceptions.BettingServiceException;
import com.sporty.f1betting.application.port.BetRepositoryPort;
import com.sporty.f1betting.application.port.EventFetcherPort;
import com.sporty.f1betting.application.port.UserRepositoryPort;
import com.sporty.f1betting.domain.enums.Odds;
import com.sporty.f1betting.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class BettingServiceTest {

    @Mock
    private UserRepositoryPort userRepo;

    @Mock
    private BetRepositoryPort betRepo;

    @Mock
    private EventFetcherPort eventFetcher;

    @InjectMocks
    private BettingService bettingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPlaceBet_Success() {
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        double amount = 100.0;

        User user = new User(userId, 200.0);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user.debit(amount));
        when(betRepo.save(any(Bet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Bet bet = bettingService.placeBet(userId, eventId, driverId, amount);

        assertNotNull(bet);
        assertEquals(userId, bet.userId());
        assertEquals(eventId, bet.eventId());
        assertEquals(driverId, bet.driverId());
        assertEquals(amount, bet.amount());
        verify(userRepo, times(1)).save(any(User.class));
        verify(betRepo, times(1)).save(any(Bet.class));
    }

    @Test
    void testPlaceBet_UserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BettingServiceException.class, () -> bettingService.placeBet(userId, UUID.randomUUID(), UUID.randomUUID(), 100.0));
        verify(userRepo, never()).save(any(User.class));
        verify(betRepo, never()).save(any(Bet.class));
    }

    @Test
    void testListEvents() {
        F1Event event = new F1Event(UUID.randomUUID(), 123, "Monaco", "Monaco", "RACE", 2023);
        Driver driver = new Driver(UUID.randomUUID(), "Lewis Hamilton");

        when(eventFetcher.fetchEvents("RACE", 2023, "Monaco")).thenReturn(List.of(event));
        when(eventFetcher.fetchDriversForEvent(event.sessionKey())).thenReturn(List.of(driver));

        List<Map<String, Object>> events = bettingService.listEvents("RACE", 2023, "Monaco");


        assertEquals(1, events.size());
        assertEquals(event, events.get(0).get("event"));

        List<Map<String, Object>> drivers = (List<Map<String, Object>>) events.get(0).get("drivers");

        assertEquals(1, drivers.size());
        assertEquals(driver.id(), drivers.get(0).get("driverId"));
        verify(eventFetcher, times(1)).fetchEvents("RACE", 2023, "Monaco");
        verify(eventFetcher, times(1)).fetchDriversForEvent(123);
    }

    @Test
    void testSimulateOutcome() {
        UUID eventId = UUID.randomUUID();
        UUID winningDriverId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Bet winningBet = new Bet(UUID.randomUUID(), userId, eventId, winningDriverId, 100.0, Odds.THREE, Bet.BetStatus.PLACED);
        Bet losingBet = new Bet(UUID.randomUUID(), userId, eventId, UUID.randomUUID(), 100.0, Odds.TWO, Bet.BetStatus.PLACED);
        User user = new User(userId, 200.0);

        when(betRepo.findByEventId(eventId)).thenReturn(List.of(winningBet, losingBet));
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        bettingService.simulateOutcome(eventId, winningDriverId);

        verify(userRepo, times(1)).save(user.credit(winningBet.winnings()));
        verify(betRepo, times(1)).save(winningBet.markWon());
        verify(betRepo, times(1)).save(losingBet.markLost());
    }
}
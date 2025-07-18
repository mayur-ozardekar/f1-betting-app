package com.sporty.f1betting.api.controller;

import com.sporty.f1betting.api.dto.PlaceBetRequest;
import com.sporty.f1betting.api.dto.SimulateRequest;
import com.sporty.f1betting.application.service.BettingService;
import com.sporty.f1betting.domain.enums.Odds;
import com.sporty.f1betting.domain.model.Bet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BettingControllerTest {

    private BettingService bettingService;
    private BettingController bettingController;

    @BeforeEach
    void setUp() {
        bettingService = mock(BettingService.class);
        bettingController = new BettingController(bettingService);
    }

    @Test
    void testPlaceBet() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        double amount = 100.0;

        PlaceBetRequest request = new PlaceBetRequest(userId, eventId, driverId, amount);
        Bet mockBet = new Bet(id, userId, eventId, driverId, amount, Odds.FOUR, Bet.BetStatus.PLACED);

        when(bettingService.placeBet(any(UUID.class), any(UUID.class), any(UUID.class), anyDouble())).thenReturn(mockBet);

        ResponseEntity<Bet> response = bettingController.placeBet(request);

        assertEquals(mockBet, response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testListEvents() {
        List<Map<String, Object>> mockEvents = List.of(
                Map.of("eventId", 1, "name", "Event 1"),
                Map.of("eventId", 2, "name", "Event 2")
        );

        when(bettingService.listEvents(anyString(), anyInt(), anyString())).thenReturn(mockEvents);

        ResponseEntity<List<Map<String, Object>>> response = bettingController.listEvents("RACE", 2025, "USA");

        assertEquals(mockEvents, response.getBody());
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void testSimulate() {
        UUID eventId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        SimulateRequest request = new SimulateRequest(eventId, driverId);

        Mockito.doNothing().when(bettingService).simulateOutcome(any(UUID.class), any(UUID.class));

        ResponseEntity<Void> response = bettingController.simulate(request);

        assertEquals(200, response.getStatusCode().value());
    }
}
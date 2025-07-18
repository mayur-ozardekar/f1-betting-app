package com.sporty.f1betting.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.f1betting.api.dto.PlaceBetRequest;
import com.sporty.f1betting.api.dto.SimulateRequest;
import com.sporty.f1betting.application.service.BettingService;
import com.sporty.f1betting.domain.enums.Odds;
import com.sporty.f1betting.domain.model.Bet;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BettingControllerContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BettingService bettingService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPlaceBet() throws Exception {
        UUID id = UUID.fromString("b84e3e4d-a873-4e97-a938-3f9e7c97ee05");
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID eventId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        double amount = 100.0;

        PlaceBetRequest request = new PlaceBetRequest(userId, eventId, driverId, amount);
        Bet mockBet = new Bet(id, userId, eventId, driverId, amount, Odds.FOUR, Bet.BetStatus.PLACED);

        Mockito.when(bettingService.placeBet(any(UUID.class), any(UUID.class), any(UUID.class), anyDouble()))
                .thenReturn(mockBet);

        mockMvc.perform(post("/api/bets/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testListEvents() throws Exception {
        List<Map<String, Object>> mockEvents = List.of(
                Map.of("eventId", 1, "name", "Event 1"),
                Map.of("eventId", 2, "name", "Event 2")
        );

        Mockito.when(bettingService.listEvents(anyString(), any(), anyString())).thenReturn(mockEvents);

        mockMvc.perform(get("/api/bets/events")
                        .param("sessionType", "RACE")
                        .param("year", "2025")
                        .param("country", "USA"))
                .andExpect(status().isOk());
    }

    @Test
    void testSimulate() throws Exception {
        UUID eventId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();

        SimulateRequest request = new SimulateRequest(eventId, driverId);

        Mockito.doNothing().when(bettingService).simulateOutcome(any(UUID.class), any(UUID.class));

        mockMvc.perform(post("/api/bets/simulate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
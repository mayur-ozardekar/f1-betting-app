package com.sporty.f1betting.api.controller;

import com.sporty.f1betting.api.dto.PlaceBetRequest;
import com.sporty.f1betting.api.dto.SimulateRequest;
import com.sporty.f1betting.application.service.BettingService;
import com.sporty.f1betting.domain.model.Bet;

import org.springframework.http.ResponseEntity;

import java.util.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bets")
public class BettingController {

    private final BettingService bettingService;

    public BettingController(BettingService bettingService) {
        this.bettingService = bettingService;
    }

    @PostMapping("/place")
    public ResponseEntity<Bet> placeBet(@RequestBody PlaceBetRequest request) {
        Bet bet = bettingService.placeBet(request.userId(), request.eventId(), request.driverId(), request.amount());
        return ResponseEntity.ok(bet);
    }

    @GetMapping("/events")
    public ResponseEntity<List<Map<String, Object>>> listEvents(
            @RequestParam(required = false) String sessionType,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country
    ) {
        return ResponseEntity.ok(bettingService.listEvents(sessionType, year, country));
    }

    @PostMapping("/simulate")
    public ResponseEntity<Void> simulate(@RequestBody SimulateRequest request) {
        bettingService.simulateOutcome(request.eventId(), request.winningDriverId());
        return ResponseEntity.ok().build();
    }
}

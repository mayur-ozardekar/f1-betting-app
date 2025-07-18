package com.sporty.f1betting.api.dto;

import java.util.UUID;

public record PlaceBetRequest(UUID userId, UUID eventId, UUID driverId, double amount) {}

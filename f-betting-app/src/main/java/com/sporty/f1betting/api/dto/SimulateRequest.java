package com.sporty.f1betting.api.dto;

import java.util.UUID;

public record SimulateRequest(UUID eventId, UUID winningDriverId) {}

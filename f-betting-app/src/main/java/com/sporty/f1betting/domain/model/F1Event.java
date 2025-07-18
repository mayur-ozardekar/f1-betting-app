package com.sporty.f1betting.domain.model;

import java.util.UUID;

public record F1Event(
        UUID id,                 // internal app reference
        int sessionKey,       // OpenF1 key
        String name,
        String country,
        String sessionType,
        int year
) {}



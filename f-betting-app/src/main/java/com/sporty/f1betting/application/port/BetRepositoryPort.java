package com.sporty.f1betting.application.port;

import com.sporty.f1betting.domain.model.Bet;
import java.util.List;
import java.util.UUID;

public interface BetRepositoryPort {
    Bet save(Bet bet);
    List<Bet> findByEventId(UUID eventId);
}

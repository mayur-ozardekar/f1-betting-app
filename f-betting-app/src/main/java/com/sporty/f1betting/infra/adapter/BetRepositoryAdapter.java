package com.sporty.f1betting.infra.adapter;

import com.sporty.f1betting.application.port.BetRepositoryPort;
import com.sporty.f1betting.domain.model.Bet;
import com.sporty.f1betting.domain.entities.BetEntity;
import com.sporty.f1betting.infra.repository.BetRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BetRepositoryAdapter implements BetRepositoryPort {

    private final BetRepository repo;

    public BetRepositoryAdapter(BetRepository repo) {
        this.repo = repo;
    }

    @Override
    public Bet save(Bet bet) {
        BetEntity entity = new BetEntity(
            bet.id(), bet.userId(), bet.eventId(), bet.driverId(),
            bet.amount(), bet.odds(), bet.status()
        );
        BetEntity saved = repo.save(entity);
        return new Bet(saved.getId(), saved.getUserId(), saved.getEventId(),
                saved.getDriverId(), saved.getAmount(), saved.getOdds(), saved.getStatus());
    }

    @Override
    public List<Bet> findByEventId(UUID eventId) {
        return repo.findByEventId(eventId).stream()
                .map(b -> new Bet(b.getId(), b.getUserId(), b.getEventId(), b.getDriverId(),
                        b.getAmount(), b.getOdds(), b.getStatus()))
                .collect(Collectors.toList());
    }
}

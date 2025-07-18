package com.sporty.f1betting.infra.adapter;

import com.sporty.f1betting.domain.entities.BetEntity;
import com.sporty.f1betting.domain.enums.Odds;
import com.sporty.f1betting.domain.model.Bet;
import com.sporty.f1betting.infra.repository.BetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BetRepositoryAdapterTest {

    @Mock
    private BetRepository betRepository;

    @InjectMocks
    private BetRepositoryAdapter betRepositoryAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();
        UUID driverId = UUID.randomUUID();
        double amount = 100.0;

        Bet bet = new Bet(id, userId, eventId, driverId, amount, Odds.FOUR, Bet.BetStatus.PLACED);
        BetEntity betEntity = new BetEntity(id, userId, eventId, driverId, amount, Odds.FOUR, Bet.BetStatus.PLACED);

        when(betRepository.save(any(BetEntity.class))).thenReturn(betEntity);

        Bet savedBet = betRepositoryAdapter.save(bet);

        assertEquals(bet.id(), savedBet.id());
        assertEquals(bet.userId(), savedBet.userId());
        assertEquals(bet.eventId(), savedBet.eventId());
        assertEquals(bet.driverId(), savedBet.driverId());
        assertEquals(bet.amount(), savedBet.amount());
        assertEquals(bet.odds(), savedBet.odds());
        assertEquals(bet.status(), savedBet.status());

        verify(betRepository, times(1)).save(any(BetEntity.class));
    }

    @Test
    void testFindByEventId() {
        UUID eventId = UUID.randomUUID();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        BetEntity betEntity1 = new BetEntity(id1, UUID.randomUUID(), eventId, UUID.randomUUID(), 50.0, Odds.TWO, Bet.BetStatus.PLACED);
        BetEntity betEntity2 = new BetEntity(id2, UUID.randomUUID(), eventId, UUID.randomUUID(), 100.0, Odds.THREE, Bet.BetStatus.WON);

        when(betRepository.findByEventId(eventId)).thenReturn(List.of(betEntity1, betEntity2));

        List<Bet> bets = betRepositoryAdapter.findByEventId(eventId);

        assertEquals(2, bets.size());
        assertEquals(id1, bets.get(0).id());
        assertEquals(id2, bets.get(1).id());

        verify(betRepository, times(1)).findByEventId(eventId);
    }
}
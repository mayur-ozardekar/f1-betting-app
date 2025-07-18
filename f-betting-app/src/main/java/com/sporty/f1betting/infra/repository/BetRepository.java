package com.sporty.f1betting.infra.repository;

import com.sporty.f1betting.domain.entities.BetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BetRepository extends JpaRepository<BetEntity, UUID> {
    List<BetEntity> findByEventId(UUID eventId);
}

package com.sporty.f1betting.infra.repository;

import com.sporty.f1betting.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {}

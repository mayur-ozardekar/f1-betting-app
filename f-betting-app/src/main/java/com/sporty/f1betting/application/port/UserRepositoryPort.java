package com.sporty.f1betting.application.port;

import com.sporty.f1betting.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {
    Optional<User> findById(UUID userId);
    User save(User user);
    List<User> saveAll(User... users);
}

package com.sporty.f1betting.infra.adapter;

import com.sporty.f1betting.application.port.UserRepositoryPort;
import com.sporty.f1betting.domain.model.User;
import com.sporty.f1betting.domain.entities.UserEntity;
import com.sporty.f1betting.infra.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository repo;

    public UserRepositoryAdapter(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return repo.findById(userId)
                .map(entity -> new User(entity.getId(), entity.getBalance()));
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(user.id(), user.balance());
        UserEntity saved = repo.save(entity);
        return new User(saved.getId(), saved.getBalance());
    }

    @Override
    public List<User> saveAll(User... users) {
        List<UserEntity> entities = java.util.Arrays.stream(users)
                .map(u -> new UserEntity(u.id(), u.balance()))
                .toList();
        List<UserEntity> savedEntities = repo.saveAll(entities);

        return savedEntities.stream()
                .map(e -> new User(e.getId(), e.getBalance()))
                .toList();
    }
}

package com.sporty.f1betting.config;

import com.sporty.f1betting.application.port.UserRepositoryPort;
import com.sporty.f1betting.domain.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DataSeeder {

    private final UserRepositoryPort userRepository;

    public static final UUID DEFAULT_USER_ID_1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final UUID DEFAULT_USER_ID_2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID DEFAULT_USER_ID_3 = UUID.fromString("33333333-3333-3333-3333-333333333333");

    public DataSeeder(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() {
        userRepository.saveAll(
                new User(DEFAULT_USER_ID_1, 100.0),
                new User(DEFAULT_USER_ID_2, 100.0),
                new User(DEFAULT_USER_ID_3, 100.0)
        );
    }
}

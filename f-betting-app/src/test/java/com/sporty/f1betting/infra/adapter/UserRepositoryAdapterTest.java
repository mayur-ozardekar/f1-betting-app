package com.sporty.f1betting.infra.adapter;

import com.sporty.f1betting.domain.entities.UserEntity;
import com.sporty.f1betting.domain.model.User;
import com.sporty.f1betting.infra.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        UUID userId = UUID.randomUUID();
        UserEntity userEntity = new UserEntity(userId, 100.0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        Optional<User> user = userRepositoryAdapter.findById(userId);

        assertTrue(user.isPresent());
        assertEquals(userId, user.get().id());
        assertEquals(100.0, user.get().balance());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testSave() {
        UUID userId = UUID.randomUUID();
        User user = new User(userId, 200.0);
        UserEntity userEntity = new UserEntity(userId, 200.0);

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        User savedUser = userRepositoryAdapter.save(user);

        assertEquals(userId, savedUser.id());
        assertEquals(200.0, savedUser.balance());

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testSaveAll() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        User user1 = new User(userId1, 300.0);
        User user2 = new User(userId2, 400.0);

        UserEntity userEntity1 = new UserEntity(userId1, 300.0);
        UserEntity userEntity2 = new UserEntity(userId2, 400.0);

        when(userRepository.saveAll(anyList())).thenReturn(List.of(userEntity1, userEntity2));

        List<User> savedUsers = userRepositoryAdapter.saveAll(user1, user2);

        assertEquals(2, savedUsers.size());
        assertEquals(userId1, savedUsers.get(0).id());
        assertEquals(300.0, savedUsers.get(0).balance());
        assertEquals(userId2, savedUsers.get(1).id());
        assertEquals(400.0, savedUsers.get(1).balance());

        verify(userRepository, times(1)).saveAll(anyList());
    }
}
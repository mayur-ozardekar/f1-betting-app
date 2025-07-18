package com.sporty.f1betting.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class UserEntity {
    @Id
    private UUID id;
    private double balance;

    public UserEntity() {}

    public UserEntity(UUID id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }
}

package com.sporty.f1betting.domain.model;

import com.sporty.f1betting.api.exceptions.BettingServiceException;
import java.util.UUID;

public record User(UUID id, double balance) {

    public User {
        if (balance < 0) {
            throw new BettingServiceException("Balance cannot be negative.");
        }
    }

    public User(UUID id) {
        this(id, 100.0); // initial gift
    }

    public User debit(double amount) {
        if (balance < amount) throw new BettingServiceException("Insufficient balance");
        return new User(id, balance - amount);
    }

    public User credit(double amount) {
        return new User(id, balance + amount);
    }
}

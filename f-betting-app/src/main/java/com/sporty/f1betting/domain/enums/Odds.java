package com.sporty.f1betting.domain.enums;

import java.util.concurrent.ThreadLocalRandom;

public enum Odds {
    TWO(2), THREE(3), FOUR(4);

    private final int value;

    Odds(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Odds random() {
        Odds[] values = values();
        return values[ThreadLocalRandom.current().nextInt(values.length)];
    }
}

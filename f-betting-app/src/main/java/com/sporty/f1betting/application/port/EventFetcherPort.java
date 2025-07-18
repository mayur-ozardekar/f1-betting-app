package com.sporty.f1betting.application.port;

import com.sporty.f1betting.domain.model.Driver;
import com.sporty.f1betting.domain.model.F1Event;

import java.util.List;

public interface EventFetcherPort {
    List<F1Event> fetchEvents(String sessionType, Integer year, String country);
    List<Driver> fetchDriversForEvent(int sessionKey);
}

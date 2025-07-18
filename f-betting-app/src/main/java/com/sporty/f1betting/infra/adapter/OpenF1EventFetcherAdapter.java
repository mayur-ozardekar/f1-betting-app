package com.sporty.f1betting.infra.adapter;

import com.fasterxml.jackson.core.JsonParseException;
import com.sporty.f1betting.application.port.EventFetcherPort;
import com.sporty.f1betting.domain.model.Driver;
import com.sporty.f1betting.domain.model.F1Event;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.DecodingException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class OpenF1EventFetcherAdapter implements EventFetcherPort {

    private final WebClient client;

    public OpenF1EventFetcherAdapter(WebClient client) {
        this.client = client;
    }

    @Override
    public List<F1Event> fetchEvents(String sessionType, Integer year, String country) {
        try {
            List<Map> sessions = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/sessions")
                            .queryParamIfPresent("year", Optional.ofNullable(year))
                            .queryParamIfPresent("country_name", Optional.ofNullable(country))
                            .queryParamIfPresent("session_type", Optional.ofNullable(sessionType))
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map>>() {})
                    .blockOptional()
                    .orElse(Collections.emptyList());

            return sessions.stream()
                    .map(m -> new F1Event(
                            UUID.randomUUID(),
                            Optional.ofNullable((Number) m.get("session_key")).map(Number::intValue).orElse(0),
                            Optional.ofNullable((String) m.get("location")).orElse("Unnamed Event"),
                            Optional.ofNullable((String) m.get("country_name")).orElse("Unknown country"),
                            Optional.ofNullable((String) m.get("session_type")).orElse("Unknown session type"),
                            Optional.ofNullable((Number) m.get("year")).map(Number::intValue).orElse(1990)
                    ))
                    .collect(Collectors.toList());
        } catch (WebClientResponseException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Driver> fetchDriversForEvent(int sessionKey) {
        try {
            List<Map> drivers = client.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/drivers")
                            .queryParam("session_key", sessionKey)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map>>() {})
                    .blockOptional()
                    .orElse(Collections.emptyList());

            return drivers.stream()
                    .map(d -> new Driver(
                            UUID.nameUUIDFromBytes(("driver-" + d.get("driver_number")).getBytes()),
                            (String) d.getOrDefault("full_name", "Unknown Driver")
                    ))
                    .collect(Collectors.toList());
        } catch (WebClientResponseException e) {
            return Collections.emptyList();
        }
    }
}
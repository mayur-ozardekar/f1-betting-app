package com.sporty.f1betting.infra.adapter;

import com.sporty.f1betting.domain.model.F1Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings({"Unchecked", "rawtypes"})
class OpenF1EventFetcherAdapterTest {

    @Mock
    private WebClient mockWebClient;

    @Mock
    private WebClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec mockRequestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec mockResponseSpec;

    private OpenF1EventFetcherAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        adapter = new OpenF1EventFetcherAdapter(mockWebClient);

        // Mock the WebClient chain
        when(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri(any(Function.class))).thenReturn(mockRequestHeadersSpec);
        when(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec);
    }

    @Test
    void testFetchEvents_success() {
        List<Map<String, Object>> mockSessions = List.of(
                Map.of("session_key", 1, "location", "Monaco", "country_name", "Monaco", "session_type", "Race", "year", 2025)
        );

        when(mockResponseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockSessions));

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Monaco", events.get(0).name());
        assertEquals(1, events.get(0).sessionKey()); // Added assertion for sessionKey
    }

    @Test
    void testFetchEvents_handlesException() {
        // Ensure the ParameterizedTypeReference matches the expected type
        when(mockResponseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenThrow(new WebClientResponseException(429, "Too Many Requests", null, null, null)); // Provide a concrete exception instance

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    void testFetchEvents_emptyResponse() {
        when(mockResponseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(List.of()));

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    void testFetchEvents_nullResponse() {
        when(mockResponseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.empty());

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    void testFetchEvents_multipleEvents() {
        List<Map<String, Object>> mockSessions = List.of(
                Map.of("session_key", 1, "location", "Monaco", "country_name", "Monaco", "session_type", "Race", "year", 2025),
                Map.of("session_key", 2, "location", "Silverstone", "country_name", "UK", "session_type", "Race", "year", 2025)
        );

        when(mockResponseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(mockSessions));

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertEquals(2, events.size());
        assertEquals("Monaco", events.get(0).name());
        assertEquals("Silverstone", events.get(1).name());
    }

    @Test
    void testFetchEvents_specificException() {
        when(mockResponseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenThrow(new WebClientResponseException(404, "Not Found", null, null, null));

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }
}

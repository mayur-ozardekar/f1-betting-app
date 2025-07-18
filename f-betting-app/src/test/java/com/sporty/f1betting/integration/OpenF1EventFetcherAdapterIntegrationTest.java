package com.sporty.f1betting.integration;

import com.sporty.f1betting.domain.model.Driver;
import com.sporty.f1betting.domain.model.F1Event;
import com.sporty.f1betting.infra.adapter.OpenF1EventFetcherAdapter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenF1EventFetcherAdapterIntegrationTest {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private ClientAndServer mockServer;

    @BeforeEach
    void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1080);
    }

    @AfterEach
    void stopMockServer() {
        mockServer.stop();
    }

    @Test
    void testFetchEvents_withMockServer() {
        // Set up MockServer expectations
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/sessions")
                        .withQueryStringParameter("session_type", "Race")
                        .withQueryStringParameter("year", "2025")
                        .withQueryStringParameter("country_name", "Monaco")
        ).respond(
                response()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        [
                            {
                                "session_key": 1,
                                "location": "Monaco",
                                "country_name": "Monaco",
                                "session_type": "Race",
                                "year": 2025
                            }
                        ]
                        """)
        );

        OpenF1EventFetcherAdapter adapter = new OpenF1EventFetcherAdapter(webClientBuilder.baseUrl("http://localhost:1080").build());

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Monaco", events.get(0).name());
    }

    @Test
    void testFetchEvents_withEmptyResponse() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/sessions")
        ).respond(
                response()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")
        );

        OpenF1EventFetcherAdapter adapter = new OpenF1EventFetcherAdapter(webClientBuilder.baseUrl("http://localhost:1080").build());

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    void testFetchEvents_withMissingFields() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/sessions")
        ).respond(
                response()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    [
                        {
                            "session_key": 2
                        }
                    ]
                    """)
        );

        OpenF1EventFetcherAdapter adapter = new OpenF1EventFetcherAdapter(webClientBuilder.baseUrl("http://localhost:1080").build());

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals("Unnamed Event", events.get(0).name());
        assertEquals("Unknown country", events.get(0).country());
    }

    @Test
    void testFetchEvents_withServerError() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/sessions")
        ).respond(
                response()
                        .withStatusCode(500)
        );

        OpenF1EventFetcherAdapter adapter = new OpenF1EventFetcherAdapter(webClientBuilder.baseUrl("http://localhost:1080").build());

        List<F1Event> events = adapter.fetchEvents("Race", 2025, "Monaco");

        assertNotNull(events);
        assertEquals(0, events.size());
    }

    @Test
    void testFetchDriversForEvent_withValidResponse() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/drivers")
                        .withQueryStringParameter("session_key", "1")
        ).respond(
                response()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    [
                        {
                            "driver_number": 44,
                            "full_name": "Lewis Hamilton"
                        },
                        {
                            "driver_number": 33,
                            "full_name": "Max Verstappen"
                        }
                    ]
                    """)
        );

        OpenF1EventFetcherAdapter adapter = new OpenF1EventFetcherAdapter(webClientBuilder.baseUrl("http://localhost:1080").build());

        List<Driver> drivers = adapter.fetchDriversForEvent(1);

        assertNotNull(drivers);
        assertEquals(2, drivers.size());
        assertEquals("Lewis Hamilton", drivers.get(0).fullName());
        assertEquals("Max Verstappen", drivers.get(1).fullName());
    }

    @Test
    void testFetchDriversForEvent_withEmptyResponse() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/drivers")
                        .withQueryStringParameter("session_key", "1")
        ).respond(
                response()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")
        );

        OpenF1EventFetcherAdapter adapter = new OpenF1EventFetcherAdapter(webClientBuilder.baseUrl("http://localhost:1080").build());

        List<Driver> drivers = adapter.fetchDriversForEvent(1);

        assertNotNull(drivers);
        assertEquals(0, drivers.size());
    }

    @Test
    void testFetchDriversForEvent_withMissingFields() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/drivers")
                        .withQueryStringParameter("session_key", "1")
        ).respond(
                response()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                    [
                        {
                            "driver_number": 44
                        }
                    ]
                    """)
        );

        OpenF1EventFetcherAdapter adapter = new OpenF1EventFetcherAdapter(webClientBuilder.baseUrl("http://localhost:1080").build());

        List<Driver> drivers = adapter.fetchDriversForEvent(1);

        assertNotNull(drivers);
        assertEquals(1, drivers.size());
        assertEquals("Unknown Driver", drivers.get(0).fullName());
    }

    @Test
    void testFetchDriversForEvent_withServerError() {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/drivers")
                        .withQueryStringParameter("session_key", "1")
        ).respond(
                response()
                        .withStatusCode(500)
        );

        OpenF1EventFetcherAdapter adapter = new OpenF1EventFetcherAdapter(webClientBuilder.baseUrl("http://localhost:1080").build());

        List<Driver> drivers = adapter.fetchDriversForEvent(1);

        assertNotNull(drivers);
        assertEquals(0, drivers.size());
    }
}
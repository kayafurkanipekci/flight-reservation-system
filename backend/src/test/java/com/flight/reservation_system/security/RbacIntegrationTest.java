package com.flight.reservation_system.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flight.reservation_system.BaseIntegrationTest;

class RbacIntegrationTest extends BaseIntegrationTest {

    private static final String AIRPORT_BODY = """
            { "name": "Rbac Airport", "iataCode": "RBC", "city": "Test", "country": "Turkiye" }
            """;

    @Test
    void passenger_creatingAirport_shouldReturn403() throws Exception {
        String token = register("rbac-passenger@flight.com", "pass12345", "Rbac");

        mockMvc.perform(post("/api/airports")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AIRPORT_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_creatingAirport_shouldSucceed() throws Exception {
        String token = login("admin@flight.com", "password");

        mockMvc.perform(post("/api/airports")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AIRPORT_BODY))
                .andExpect(status().isOk());
    }

    @Test
    void noToken_creatingAirport_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(AIRPORT_BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void passenger_creatingFlight_shouldReturn403() throws Exception {
        String token = register("rbac-passenger2@flight.com", "pass12345", "Rbac2");
        mockMvc.perform(post("/api/flights")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"flightNumber":"RBC1","airplaneId":1,"departureAirportId":1,
                                "arrivalAirportId":2,"departureTime":"2027-06-01T10:00:00",
                                "arrivalTime":"2027-06-01T12:00:00","status":"SCHEDULED"}
                                """))
                .andExpect(status().isForbidden());
    }
}
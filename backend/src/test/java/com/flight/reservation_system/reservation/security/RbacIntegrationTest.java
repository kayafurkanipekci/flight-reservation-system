package com.flight.reservation_system.reservation.security;

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
}
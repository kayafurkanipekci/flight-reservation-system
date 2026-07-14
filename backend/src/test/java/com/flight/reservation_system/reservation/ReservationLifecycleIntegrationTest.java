package com.flight.reservation_system.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.flight.reservation_system.BaseIntegrationTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReservationLifecycleIntegrationTest extends BaseIntegrationTest {

    private static String passengerToken;
    private static String passenger2Token;
    private static String adminToken;
    private static Long flightId;
    private static Long reservationId;

    @BeforeEach
    void setUp() throws Exception {
        if (adminToken == null) {
            adminToken = login("admin@flight.com", "password");
        }
        if (passengerToken == null) {
            passengerToken = register("lifecycle-passenger1@flight.com", "pass12345", "Lifecycle1");
        }
        if (passenger2Token == null) {
            passenger2Token = register("lifecycle-passenger2@flight.com", "pass67890", "Lifecycle2");
        }
    }

    @Test
    @Order(1)
    void admin_createsFlight_shouldSucceed() throws Exception {
        String body = """
                {
                  "flightNumber": "LC101",
                  "airplaneId": 1,
                  "departureAirportId": 1,
                  "arrivalAirportId": 2,
                  "departureTime": "2027-06-01T10:00:00",
                  "arrivalTime": "2027-06-01T12:00:00",
                  "status": "SCHEDULED"
                }
                """;

        String response = mockMvc.perform(post("/api/flights")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        flightId = json.get("id").asLong();
    }

    @Test
    @Order(2)
    void passenger_createsReservation_shouldSucceed() throws Exception {
        String body = """
                {
                  "tickets": [
                    { "flightId": %d, "seatNumber": "1A" }
                  ]
                }
                """.formatted(flightId);

        String response = mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + passengerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.tickets[0].seatNumber").value("1A"))
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        reservationId = json.get("id").asLong();
    }

    @Test
    @Order(3)
    void passenger_bookingSameSeatAgain_shouldReturn409() throws Exception {
        String body = """
                {
                  "tickets": [
                    { "flightId": %d, "seatNumber": "1A" }
                  ]
                }
                """.formatted(flightId);

        mockMvc.perform(post("/api/reservations")
                        .header("Authorization", "Bearer " + passengerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(4)
    void passenger_viewsOwnReservations_shouldIncludeCreatedOne() throws Exception {
        mockMvc.perform(get("/api/reservations/my")
                        .header("Authorization", "Bearer " + passengerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservationId));
    }

    @Test
    @Order(5)
    void otherPassenger_cancelingSomeoneElsesReservation_shouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/reservations/" + reservationId)
                        .header("Authorization", "Bearer " + passenger2Token))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    void passenger_cancelsOwnReservation_shouldSucceed() throws Exception {
        mockMvc.perform(delete("/api/reservations/" + reservationId)
                        .header("Authorization", "Bearer " + passengerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELED"));
    }

    @Test
    @Order(7)
    void passenger_cancelingAlreadyCanceledReservation_shouldReturn409() throws Exception {
        mockMvc.perform(delete("/api/reservations/" + reservationId)
                        .header("Authorization", "Bearer " + passengerToken))
                .andExpect(status().isConflict());
    }
}
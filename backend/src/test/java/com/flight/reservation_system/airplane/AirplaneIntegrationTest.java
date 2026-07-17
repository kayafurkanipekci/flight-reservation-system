package com.flight.reservation_system.airplane;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.flight.reservation_system.BaseIntegrationTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AirplaneIntegrationTest extends BaseIntegrationTest {

    private String adminToken;

    @BeforeAll
    void setUpAdmin() throws Exception {
        adminToken = login("admin@flight.com", "password");
    }

    @Test
    void createAirplane_returnsDtoResponseShape() throws Exception {
        String body = """
                {
                  "model": "Boeing 777",
                  "tailNumber": "TC-DTO1",
                  "capacity": 350,
                  "airline": "THY"
                }
                """;

        mockMvc.perform(post("/api/airplanes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.model").value("Boeing 777"))
                .andExpect(jsonPath("$.tailNumber").value("TC-DTO1"))
                .andExpect(jsonPath("$.capacity").value(350))
                .andExpect(jsonPath("$.airline").value("THY"))
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void getAirplaneById_returnsDto() throws Exception {
        String createBody = """
                {
                  "model": "Airbus A350",
                  "tailNumber": "TC-DTO2",
                  "capacity": 320,
                  "airline": "Pegasus"
                }
                """;
        String response = mockMvc.perform(post("/api/airplanes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/airplanes/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.model").value("Airbus A350"))
                .andExpect(jsonPath("$.tailNumber").value("TC-DTO2"))
                .andExpect(jsonPath("$.capacity").value(320))
                .andExpect(jsonPath("$.airline").value("Pegasus"));
    }

    @Test
    void getAllAirplanes_returnsDtoListIncludingCreated() throws Exception {
        String body = """
                {
                  "model": "Boeing 737",
                  "tailNumber": "TC-DTO3",
                  "capacity": 180,
                  "airline": "AnadoluJet"
                }
                """;

        mockMvc.perform(post("/api/airplanes")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/airplanes")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.tailNumber == 'TC-DTO3')]").isArray())
                .andExpect(jsonPath("$[?(@.tailNumber == 'TC-DTO3')].model").value("Boeing 737"))
                .andExpect(jsonPath("$[?(@.tailNumber == 'TC-DTO3')].airline").value("AnadoluJet"));
    }
}

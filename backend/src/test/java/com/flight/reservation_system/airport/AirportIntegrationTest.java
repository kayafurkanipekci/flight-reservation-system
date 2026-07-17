package com.flight.reservation_system.airport;

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
class AirportIntegrationTest extends BaseIntegrationTest {

    private String adminToken;
    private Long createdAirportId;

    @BeforeAll
    void setUpAdmin() throws Exception {
        // login() BaseIntegrationTest'in protected instance method'udur;
        // PER_CLASS lifecycle ile @BeforeAll içinden instance üzerinden çağırabiliyoruz.
        adminToken = login("admin@flight.com", "password");
    }

    @Test
    void createAirport_returnsDtoResponseShape() throws Exception {
        String body = """
                {
                  "name": "Sabiha Gokcen",
                  "iataCode": "SAW",
                  "city": "Istanbul",
                  "country": "Turkiye"
                }
                """;

        mockMvc.perform(post("/api/airports")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Sabiha Gokcen"))
                .andExpect(jsonPath("$.iataCode").value("SAW"))
                .andExpect(jsonPath("$.city").value("Istanbul"))
                .andExpect(jsonPath("$.country").value("Turkiye"))
                // Entity'ye özel bir alan (audit, version vs.) sızmasın diye bilinçli şekilde yokluğunu doğrulamıyoruz;
                // buradaki asıl sözleşme: response olarak DTO'nun 5 alanı da mevcut.
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void getAirportById_returnsDto() throws Exception {
        // createAirport'ın I'den önce çalışması için; @Test sırası JUnit'te garanti değil, ID'yi burada yeniden oluşturuyoruz.
        // V3 migration DB'yi IST/ESB/ADB ile seed'lediği için benzersiz bir IATA seçiyoruz.
        String body = """
                {
                  "name": "Test Get Airport",
                  "iataCode": "GTA",
                  "city": "Test City",
                  "country": "Turkiye"
                }
                """;
        String response = mockMvc.perform(post("/api/airports")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        createdAirportId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/airports/" + createdAirportId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdAirportId))
                .andExpect(jsonPath("$.name").value("Test Get Airport"))
                .andExpect(jsonPath("$.iataCode").value("GTA"))
                .andExpect(jsonPath("$.city").value("Test City"))
                .andExpect(jsonPath("$.country").value("Turkiye"));
    }

    @Test
    void getAllAirports_returnsDtoListIncludingCreated() throws Exception {
        // Bu test bağımsız olarak bir tane daha ekler ki listenin içinde kendi kaydını garanti edebilelim.
        String body = """
                {
                  "name": "Antalya Airport",
                  "iataCode": "AYT",
                  "city": "Antalya",
                  "country": "Turkiye"
                }
                """;

        mockMvc.perform(post("/api/airports")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/airports")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                // Listenin en az bir Airport DTO'su içerdiğini ve sahip olduğu alanları doğrula.
                // Filter içindeki eşleşme: iataCode alanı AYT olan kayıt en az bir kere geçsin.
                .andExpect(jsonPath("$[?(@.iataCode == 'AYT')]").isArray())
                .andExpect(jsonPath("$[?(@.iataCode == 'AYT')].name").value("Antalya Airport"))
                .andExpect(jsonPath("$[?(@.iataCode == 'AYT')].country").value("Turkiye"));
    }
}

package com.flight.reservation_system.airport;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DtoAirportResponseTest {

    @Test
    void fromEntity_copiesAllFields() {
        Airport airport = new Airport(1L, "Istanbul Airport", "IST", "Istanbul", "Turkiye");

        DtoAirportResponse dto = DtoAirportResponse.fromEntity(airport);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Istanbul Airport");
        assertThat(dto.getIataCode()).isEqualTo("IST");
        assertThat(dto.getCity()).isEqualTo("Istanbul");
        assertThat(dto.getCountry()).isEqualTo("Turkiye");
    }
}

package com.flight.reservation_system.airplane;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DtoAirplaneResponseTest {

    @Test
    void fromEntity_copiesAllFields() {
        Airplane airplane = new Airplane(1L, "Boeing 737", "TC-TST", 180, "THY");

        DtoAirplaneResponse dto = DtoAirplaneResponse.fromEntity(airplane);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getModel()).isEqualTo("Boeing 737");
        assertThat(dto.getTailNumber()).isEqualTo("TC-TST");
        assertThat(dto.getCapacity()).isEqualTo(180);
        assertThat(dto.getAirline()).isEqualTo("THY");
    }
}

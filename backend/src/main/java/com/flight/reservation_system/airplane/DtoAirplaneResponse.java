package com.flight.reservation_system.airplane;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DtoAirplaneResponse {
    private Long id;
    private String model;
    private String tailNumber;
    private int capacity;
    private String airline;

    public static DtoAirplaneResponse fromEntity(Airplane airplane) {
        return new DtoAirplaneResponse(
                airplane.getId(),
                airplane.getModel(),
                airplane.getTailNumber(),
                airplane.getCapacity(),
                airplane.getAirline()
        );
    }
}

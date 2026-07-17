package com.flight.reservation_system.airport;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DtoAirportResponse {
    private Long id;
    private String name;
    private String iataCode;
    private String city;
    private String country;

    public static DtoAirportResponse fromEntity(Airport airport) {
        return new DtoAirportResponse(
                airport.getId(),
                airport.getName(),
                airport.getIataCode(),
                airport.getCity(),
                airport.getCountry()
        );
    }
}

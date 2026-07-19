package com.flight.reservation_system.airport;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DtoAirportResponse {
    private Long id;
    private String name;
    
    @JsonProperty("iataCode")
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
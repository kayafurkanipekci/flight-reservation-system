package com.flight.reservation_system.flight;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DtoFlightResponse {

    private Long id;
    private String flightNumber;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private FlightStatus status;
    private String airplaneModel;
    private String airplaneTailNumber;
    private String departureAirportCode;
    private String departureAirportCity;
    private String arrivalAirportCode;
    private String arrivalAirportCity;

    public static DtoFlightResponse fromEntity(Flight flight) {
        return new DtoFlightResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getStatus(),
                flight.getAirplane().getModel(),
                flight.getAirplane().getTailNumber(),
                flight.getDepartureAirport().getIataCode(),
                flight.getDepartureAirport().getCity(),
                flight.getArrivalAirport().getIataCode(),
                flight.getArrivalAirport().getCity()
        );
    }
}

package com.flight.reservation_system.ticket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DtoTicketResponse {
    private Long id;
    private String flightNumber;
    private String seatNumber;
    private Integer segmentOrder;
    private String departureAirportCode;
    private String arrivalAirportCode;

    public static DtoTicketResponse fromEntity(Ticket ticket) {
        return new DtoTicketResponse(
                ticket.getId(),
                ticket.getFlight().getFlightNumber(),
                ticket.getSeatNumber(),
                ticket.getSegmentOrder(),
                ticket.getFlight().getDepartureAirport().getIataCode(),
                ticket.getFlight().getArrivalAirport().getIataCode()
        );
    }
}
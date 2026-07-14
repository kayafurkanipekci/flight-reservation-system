package com.flight.reservation_system.reservation;

import java.util.List;

import com.flight.reservation_system.ticket.DtoTicketResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DtoReservationResponse {
    private Long id;
    private String passengerEmail;
    private ReservationStatus status;
    private List<DtoTicketResponse> tickets;
}
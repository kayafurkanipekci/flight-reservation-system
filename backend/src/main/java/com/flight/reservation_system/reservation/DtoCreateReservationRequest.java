package com.flight.reservation_system.reservation;

import java.util.List;

import com.flight.reservation_system.ticket.DtoTicketRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoCreateReservationRequest {

    @NotEmpty(message = "At least one ticket is required")
    @Valid
    private List<DtoTicketRequest> tickets;
}
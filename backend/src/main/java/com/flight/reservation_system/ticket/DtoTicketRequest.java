package com.flight.reservation_system.ticket;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoTicketRequest {

    @NotNull(message = "Flight id is required")
    private Long flightId;

    @NotBlank(message = "Seat number is required")
    private String seatNumber;
}
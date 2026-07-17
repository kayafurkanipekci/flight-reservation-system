package com.flight.reservation_system.reservation;

public record ReservationCreatedEvent(Long reservationId, String passengerEmail) {
}
package com.flight.reservation_system.ticket;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flight.reservation_system.reservation.Reservation;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByReservation(Reservation reservation);
}
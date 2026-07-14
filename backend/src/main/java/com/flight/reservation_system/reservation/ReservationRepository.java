package com.flight.reservation_system.reservation;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flight.reservation_system.user.User;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByPassenger(User passenger);
}
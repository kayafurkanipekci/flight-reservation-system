package com.flight.reservation_system.reservation;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public DtoReservationResponse createReservation(@Valid @RequestBody DtoCreateReservationRequest request) {
        return reservationService.createReservation(request);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PASSENGER')")
    public List<DtoReservationResponse> getMyReservations() {
        return reservationService.getMyReservations();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<DtoReservationResponse> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PASSENGER')")
    public DtoReservationResponse cancelReservation(@PathVariable Long id) {
        return reservationService.cancelReservation(id);
    }
}
package com.flight.reservation_system.reservation;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.flight.reservation_system.email.EmailService;

@Component
public class ReservationConfirmationListener {

    private final ReservationRepository reservationRepository;
    private final EmailService emailService;

    public ReservationConfirmationListener(ReservationRepository reservationRepository,
                                            EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationCreated(ReservationCreatedEvent event) {
        reservationRepository.findById(event.reservationId()).ifPresent(reservation -> {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
            emailService.sendReservationConfirmedEmail(event.passengerEmail(), reservation.getId());
        });
    }
}
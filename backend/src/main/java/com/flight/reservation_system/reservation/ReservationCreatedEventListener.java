package com.flight.reservation_system.reservation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class ReservationCreatedEventListener {

    private static final Logger log = LoggerFactory.getLogger(ReservationCreatedEventListener.class);

    private final JavaMailSender mailSender;

    public ReservationCreatedEventListener(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @EventListener
    public void onReservationCreated(ReservationCreatedEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.passengerEmail());
            message.setSubject("Reservation Confirmed - ButterFlight");
            message.setText("Your reservation has been confirmed. Reservation ID: " + event.reservationId());

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send reservation confirmation email to {}", event.passengerEmail(), e);
        }
    }
}
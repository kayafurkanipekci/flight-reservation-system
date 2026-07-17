package com.flight.reservation_system.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReservationConfirmedEmail(String to, Long reservationId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reservation Confirmed - #" + reservationId);
        message.setText("Your reservation #" + reservationId
                + " has been confirmed. Thank you for booking with us!");
        try {
            mailSender.send(message);
        } catch (MailException e) {
            // Mail gönderimi rezervasyon işlemini bloklamamalı; sadece logla.
            log.error("Failed to send confirmation email to {} for reservation {}", to, reservationId, e);
        }
    }
}
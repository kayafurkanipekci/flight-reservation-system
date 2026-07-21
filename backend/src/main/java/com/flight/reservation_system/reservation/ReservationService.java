package com.flight.reservation_system.reservation;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flight.reservation_system.flight.Flight;
import com.flight.reservation_system.flight.FlightRepository;
import com.flight.reservation_system.ticket.DtoTicketRequest;
import com.flight.reservation_system.ticket.DtoTicketResponse;
import com.flight.reservation_system.ticket.Ticket;
import com.flight.reservation_system.ticket.TicketRepository;
import com.flight.reservation_system.user.User;
import com.flight.reservation_system.user.UserRepository;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReservationService(ReservationRepository reservationRepository,
                               TicketRepository ticketRepository,
                               FlightRepository flightRepository,
                               UserRepository userRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public DtoReservationResponse createReservation(DtoCreateReservationRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User passenger = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        Reservation reservation = new Reservation();
        reservation.setPassenger(passenger);
        reservation.setStatus(ReservationStatus.PENDING);
        Reservation savedReservation = reservationRepository.save(reservation);

        List<DtoTicketRequest> ticketRequests = request.getTickets();
        List<DtoTicketResponse> ticketResponses = new java.util.ArrayList<>();
        int segmentOrder = 1;
        for (DtoTicketRequest ticketRequest : ticketRequests) {
            Flight flight = flightRepository.findById(ticketRequest.getFlightId())
                    .orElseThrow(() -> new RuntimeException("Flight not found with id: " + ticketRequest.getFlightId()));
            
            boolean seatExists = ticketRepository.existsByFlightAndSeatNumber(
                flight, 
                ticketRequest.getSeatNumber()
            );
            if (seatExists) {
                throw new DataIntegrityViolationException(
                    "Seat " + ticketRequest.getSeatNumber() + " is already booked on flight " + flight.getFlightNumber()
                );
            }
            
            Ticket ticket = new Ticket();
            ticket.setReservation(savedReservation);
            ticket.setFlight(flight);
            ticket.setSeatNumber(ticketRequest.getSeatNumber());
            ticket.setSegmentOrder(segmentOrder);
            Ticket savedTicket = ticketRepository.save(ticket);
            ticketResponses.add(DtoTicketResponse.fromEntity(savedTicket));
            segmentOrder++;
        }

        eventPublisher.publishEvent(new ReservationCreatedEvent(savedReservation.getId(), passenger.getEmail()));

        return new DtoReservationResponse(
                savedReservation.getId(),
                passenger.getEmail(),
                savedReservation.getStatus(),
                ticketResponses
        );
    }

    public List<DtoReservationResponse> getMyReservations() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User passenger = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        return reservationRepository.findByPassenger(passenger).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<DtoReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DtoReservationResponse cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + id));

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            String email = authentication.getName();
            if (!reservation.getPassenger().getEmail().equals(email)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "You can only cancel your own reservations.");
            }
        }
        
        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            throw new IllegalStateException("This reservation is already canceled.");
        }

        reservation.setStatus(ReservationStatus.CANCELED);
        Reservation saved = reservationRepository.save(reservation);
        return toResponse(saved);
    }

    private DtoReservationResponse toResponse(Reservation reservation) {
        List<DtoTicketResponse> tickets = ticketRepository.findByReservation(reservation).stream()
                .map(DtoTicketResponse::fromEntity)
                .toList();

        return new DtoReservationResponse(
                reservation.getId(),
                reservation.getPassenger().getEmail(),
                reservation.getStatus(),
                tickets
        );
    }
}
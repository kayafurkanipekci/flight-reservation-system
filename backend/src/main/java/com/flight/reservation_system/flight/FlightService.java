package com.flight.reservation_system.flight;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flight.reservation_system.airplane.Airplane;
import com.flight.reservation_system.airplane.AirplaneRepository;
import com.flight.reservation_system.airport.Airport;
import com.flight.reservation_system.airport.AirportRepository;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final AirplaneRepository airplaneRepository;

    public FlightService(FlightRepository flightRepository,
                         AirportRepository airportRepository,
                         AirplaneRepository airplaneRepository) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.airplaneRepository = airplaneRepository;
    }

    public List<DtoFlightResponse> getAllFlights() {
        return flightRepository.findAll()
                .stream()
                .map(DtoFlightResponse::fromEntity)
                .toList();
    }

    public DtoFlightResponse getFlightById(Long id) {
        return flightRepository.findById(id)
                .map(DtoFlightResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));
    }

    public DtoFlightResponse createFlight(DtoFlightRequest request) {
        Airplane airplane = airplaneRepository.findById(request.getAirplaneId())
                .orElseThrow(() -> new RuntimeException("Airplane not found with id: " + request.getAirplaneId()));
        Airport departureAirport = airportRepository.findById(request.getDepartureAirportId())
                .orElseThrow(() -> new RuntimeException("Departure airport not found with id: " + request.getDepartureAirportId()));
        Airport arrivalAirport = airportRepository.findById(request.getArrivalAirportId())
                .orElseThrow(() -> new RuntimeException("Arrival airport not found with id: " + request.getArrivalAirportId()));

        Flight flight = new Flight();
        flight.setFlightNumber(request.getFlightNumber());
        flight.setAirplane(airplane);
        flight.setDepartureAirport(departureAirport);
        flight.setArrivalAirport(arrivalAirport);
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setStatus(request.getStatus());

        return DtoFlightResponse.fromEntity(flightRepository.save(flight));
    }

    public DtoFlightResponse updateFlight(Long id, DtoFlightRequest request) {
        Flight existing = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id: " + id));

        Airplane airplane = airplaneRepository.findById(request.getAirplaneId())
                .orElseThrow(() -> new RuntimeException("Airplane not found with id: " + request.getAirplaneId()));
        Airport departureAirport = airportRepository.findById(request.getDepartureAirportId())
                .orElseThrow(() -> new RuntimeException("Departure airport not found with id: " + request.getDepartureAirportId()));
        Airport arrivalAirport = airportRepository.findById(request.getArrivalAirportId())
                .orElseThrow(() -> new RuntimeException("Arrival airport not found with id: " + request.getArrivalAirportId()));

        existing.setFlightNumber(request.getFlightNumber());
        existing.setAirplane(airplane);
        existing.setDepartureAirport(departureAirport);
        existing.setArrivalAirport(arrivalAirport);
        existing.setDepartureTime(request.getDepartureTime());
        existing.setArrivalTime(request.getArrivalTime());
        existing.setStatus(request.getStatus());

        return DtoFlightResponse.fromEntity(flightRepository.save(existing));
    }

    public void deleteFlight(Long id) {
        flightRepository.deleteById(id);
    }
}

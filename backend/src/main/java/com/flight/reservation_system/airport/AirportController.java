package com.flight.reservation_system.airport;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/airports")
// @CrossOrigin(origins = "http://localhost:5173")
public class AirportController {

    private static final Logger log = LoggerFactory.getLogger(AirportController.class);

    private final AirportService airportService;

    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping
    public List<DtoAirportResponse> getAllAirports() {
        try {
            return airportService.getAllAirports();
        } catch (Exception ex) {
            log.error("Error in GET /api/airports -> {}", ex.toString(), ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public DtoAirportResponse getAirportById(@PathVariable Long id) {
        return airportService.getAirportById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public DtoAirportResponse createAirport(@Valid @RequestBody DtoAirportRequest request) {
        return airportService.createAirport(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DtoAirportResponse updateAirport(@PathVariable Long id, @Valid @RequestBody DtoAirportRequest request) {
        return airportService.updateAirport(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAirport(@PathVariable Long id) {
        airportService.deleteAirport(id);
    }
}
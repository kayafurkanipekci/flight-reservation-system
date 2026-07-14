package com.flight.reservation_system.airplane;

import java.util.List;

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
@RequestMapping("/api/airplanes")
public class AirplaneController {

    private final AirplaneService airplaneService;

    public AirplaneController(AirplaneService airplaneService) {
        this.airplaneService = airplaneService;
    }

    @GetMapping
    public List<Airplane> getAllAirplanes() {
        return airplaneService.getAllAirplanes();
    }

    @GetMapping("/{id}")
    public Airplane getAirplaneById(@PathVariable Long id) {
        return airplaneService.getAirplaneById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Airplane createAirplane(@Valid @RequestBody DtoAirplaneRequest request) {
        return airplaneService.createAirplane(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Airplane updateAirplane(@PathVariable Long id, @Valid @RequestBody DtoAirplaneRequest request) {
        return airplaneService.updateAirplane(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAirplane(@PathVariable Long id) {
        airplaneService.deleteAirplane(id);
    }
}
package com.flight.reservation_system.airplane;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class AirplaneService {
    
    private final AirplaneRepository airportRepository; 
    
    public AirplaneService(AirplaneRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public List<Airplane> getAllAirplanes() {
        return airportRepository.findAll();
    }

    public Airplane getAirplaneById(Long id) {
    return airportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Airplane not found with id: " + id));
    }

    public Airplane createAirplane(Airplane airport) {
        return airportRepository.save(airport);
    }

    public Airplane updateAirplane(Long id, Airplane updatedAirplane) {
        Airplane existing = getAirplaneById(id);
        existing.setModel(updatedAirplane.getModel());
        existing.setTailNumber(updatedAirplane.getTailNumber());
        existing.setCapacity(updatedAirplane.getCapacity());
        existing.setAirline(updatedAirplane.getAirline());
        return airportRepository.save(existing);
    }

    public void deleteAirplane(Long id) {
        airportRepository.deleteById(id);
    }
}

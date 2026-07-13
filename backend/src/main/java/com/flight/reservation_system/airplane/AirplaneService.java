package com.flight.reservation_system.airplane;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;

    public AirplaneService(AirplaneRepository airplaneRepository) {
        this.airplaneRepository = airplaneRepository;
    }

    public List<Airplane> getAllAirplanes() {
        return airplaneRepository.findAll();
    }

    public Airplane getAirplaneById(Long id) {
        return airplaneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airplane not found with id: " + id));
    }

    public Airplane createAirplane(DtoAirplaneRequest request) {
        Airplane airplane = new Airplane();
        airplane.setModel(request.getModel());
        airplane.setTailNumber(request.getTailNumber());
        airplane.setCapacity(request.getCapacity());
        airplane.setAirline(request.getAirline());
        return airplaneRepository.save(airplane);
    }

    public Airplane updateAirplane(Long id, DtoAirplaneRequest request) {
        Airplane existing = getAirplaneById(id);
        existing.setModel(request.getModel());
        existing.setTailNumber(request.getTailNumber());
        existing.setCapacity(request.getCapacity());
        existing.setAirline(request.getAirline());
        return airplaneRepository.save(existing);
    }

    public void deleteAirplane(Long id) {
        airplaneRepository.deleteById(id);
    }
}
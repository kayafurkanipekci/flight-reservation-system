package com.flight.reservation_system.airplane;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;

    public AirplaneService(AirplaneRepository airplaneRepository) {
        this.airplaneRepository = airplaneRepository;
    }

    @Cacheable(value = "airplanes", key = "'allAirplanes'")
    public List<DtoAirplaneResponse> getAllAirplanes() {
        return airplaneRepository.findAll()
                .stream()
                .map(DtoAirplaneResponse::fromEntity)
                .toList();
    }

    public DtoAirplaneResponse getAirplaneById(Long id) {
        return airplaneRepository.findById(id)
                .map(DtoAirplaneResponse::fromEntity)
                .orElseThrow(() -> new RuntimeException("Airplane not found with id: " + id));
    }

    @CacheEvict(value = "airplanes", key = "'allAirplanes'")
    public DtoAirplaneResponse createAirplane(DtoAirplaneRequest request) {
        Airplane airplane = new Airplane();
        airplane.setModel(request.getModel());
        airplane.setTailNumber(request.getTailNumber());
        airplane.setCapacity(request.getCapacity());
        airplane.setAirline(request.getAirline());
        return DtoAirplaneResponse.fromEntity(airplaneRepository.save(airplane));
    }

    @CacheEvict(value = "airplanes", key = "'allAirplanes'")
    public DtoAirplaneResponse updateAirplane(Long id, DtoAirplaneRequest request) {
        Airplane existing = getAirplaneByIdEntity(id);
        existing.setModel(request.getModel());
        existing.setTailNumber(request.getTailNumber());
        existing.setCapacity(request.getCapacity());
        existing.setAirline(request.getAirline());
        return DtoAirplaneResponse.fromEntity(airplaneRepository.save(existing));
    }

    @CacheEvict(value = "airplanes", key = "'allAirplanes'")
    public void deleteAirplane(Long id) {
        airplaneRepository.deleteById(id);
    }

    private Airplane getAirplaneByIdEntity(Long id) {
        return airplaneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airplane not found with id: " + id));
    }
}

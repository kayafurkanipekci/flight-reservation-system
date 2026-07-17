package com.flight.reservation_system.airport;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
// ^- Bu arkadaş Spring'e bu class'ın bir service olduğunu söylüyormuş. İleride Redis'te işimize yarayacakmış
// Uygulama başlarken bunu bul ve bir tane örneğini oluştur şeklide çalışır.
public class AirportService {

    private final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Cacheable(value = "airports", key = "'allAirports'")
    public List<DtoAirportResponse> getAllAirports() {
        return airportRepository.findAll()
                .stream()
                .map(DtoAirportResponse::fromEntity)
                .toList();
    }

    public DtoAirportResponse getAirportById(Long id) {
        return airportRepository.findById(id)
                .map(DtoAirportResponse::fromEntity)
                .orElseThrow(() -> new com.flight.reservation_system.exception.ResourceNotFoundException(
                        "Airport not found with id: " + id));
    }

    @CacheEvict(value = "airports", key = "'allAirports'")
    public DtoAirportResponse createAirport(DtoAirportRequest request) {
        Airport airport = new Airport();
        airport.setName(request.getName());
        airport.setIataCode(request.getIataCode());
        airport.setCity(request.getCity());
        airport.setCountry(request.getCountry());
        return DtoAirportResponse.fromEntity(airportRepository.save(airport));
    }

    @CacheEvict(value = "airports", key = "'allAirports'")
    public DtoAirportResponse updateAirport(Long id, DtoAirportRequest request) {
        Airport existing = getAirportByIdEntity(id);
        existing.setName(request.getName());
        existing.setIataCode(request.getIataCode());
        existing.setCity(request.getCity());
        existing.setCountry(request.getCountry());
        return DtoAirportResponse.fromEntity(airportRepository.save(existing));
    }

    @CacheEvict(value = "airports", key = "'allAirports'")
    public void deleteAirport(Long id) {
        airportRepository.deleteById(id);
    }

    private Airport getAirportByIdEntity(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new com.flight.reservation_system.exception.ResourceNotFoundException(
                        "Airport not found with id: " + id));
    }
}


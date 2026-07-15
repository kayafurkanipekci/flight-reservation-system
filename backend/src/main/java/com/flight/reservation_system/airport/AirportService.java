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
    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Airport getAirportById(Long id) {
        return airportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Airport not found with id: " + id));
    }
    
    @CacheEvict(value = "airports", key = "'allAirports'")
    public Airport createAirport(DtoAirportRequest request) {
        Airport airport = new Airport();
        airport.setName(request.getName());
        airport.setIataCode(request.getIataCode());
        airport.setCity(request.getCity());
        airport.setCountry(request.getCountry());
        return airportRepository.save(airport);
    }

    @CacheEvict(value = "airports", key = "'allAirports'")
    public Airport updateAirport(Long id, DtoAirportRequest request) {
        Airport existing = getAirportById(id);
        existing.setName(request.getName());
        existing.setIataCode(request.getIataCode());
        existing.setCity(request.getCity());
        existing.setCountry(request.getCountry());
        return airportRepository.save(existing);
    }

    @CacheEvict(value = "airports", key = "'allAirports'")
    public void deleteAirport(Long id) {
        airportRepository.deleteById(id);
    }
}
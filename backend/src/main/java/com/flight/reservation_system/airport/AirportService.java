package com.flight.reservation_system.airport;

import java.util.List;

import org.springframework.stereotype.Service;

// bu belgeyi daha sonra daha detaylı incele ve anlamaya çalış.


@Service    
// ^- Bu arkadaş Spring'e bu class'ın bir service olduğunu söylüyormuş. İleride Redis'te işimize yarayacakmış
// Uygulama başlarken bunu bul ve bir tane örneğini oluştur şeklide çalışır.

public class AirportService {
    
    private final AirportRepository airportRepository; //bu field bir kere atandıktan sonra asla değiştirilemez demekmiş.

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public List<Airport> getAllAirports() {
        return airportRepository.findAll();
    }

    public Airport getAirportById(Long id) {
    return airportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Airport not found with id: " + id));
    }

    public Airport createAirport(Airport airport) {
        return airportRepository.save(airport);
    }

    public Airport updateAirport(Long id, Airport updatedAirport) {
        Airport existing = getAirportById(id);
        existing.setName(updatedAirport.getName());
        existing.setIataCode(updatedAirport.getIataCode());
        existing.setCity(updatedAirport.getCity());
        existing.setCountry(updatedAirport.getCountry());
        return airportRepository.save(existing);
    }

    public void deleteAirport(Long id) {
        airportRepository.deleteById(id);
    }
    
}



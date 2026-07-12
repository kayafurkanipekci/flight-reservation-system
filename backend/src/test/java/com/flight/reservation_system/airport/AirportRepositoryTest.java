package com.flight.reservation_system.airport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AirportRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    private AirportRepository airportRepository;

    @Test
    void shouldSaveAndRetrieveAirport() {
        Airport airport = new Airport();
        airport.setName("Test Airport");
        airport.setIataCode("TST");
        airport.setCity("Test City");
        airport.setCountry("Test Country");

        Airport saved = airportRepository.save(airport);

        assertThat(saved.getId()).isNotNull();
        assertThat(airportRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void shouldNotAllowDuplicateIataCode() {
    Airport first = new Airport();
    first.setName("Istanbul Airport");
    first.setIataCode("DUP");
    first.setCity("Istanbul");
    first.setCountry("Turkiye");
    airportRepository.saveAndFlush(first);

    Airport duplicate = new Airport();
    duplicate.setName("Sahte Havalimani");
    duplicate.setIataCode("DUP");
    duplicate.setCity("Ankara");
    duplicate.setCountry("Turkiye");

    assertThatThrownBy(() -> airportRepository.saveAndFlush(duplicate))
            .isInstanceOf(DataIntegrityViolationException.class);
    }
    
}